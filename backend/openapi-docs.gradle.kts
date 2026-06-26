import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.tasks.Copy
import java.io.File

/**
 * OpenAPI(Swagger) 문서 생성을 커스텀하기 위한 스크립트입니다.
 * RestDocs를 통해 생성된 기본 OpenAPI Spec(JSON)에 보안 설정, 예시 데이터, 에러 메시지 상세화 등을 추가합니다.
 */

// 운영(prod) 프로필이 아닌 경우에만 OpenAPI 문서 커스텀 로직을 수행합니다.
val isProdProfile = project.findProperty("profile") == "prod"

if (!isProdProfile) {
    // 1. OpenAPI Spec에 보안 설정 및 부가 정보를 주입하는 커스텀 태스크 등록
    tasks.register("injectOpenApiSecurity") {
        dependsOn("openapi3") // epages/restdocs-api-spec 플러그인의 openapi3 태스크 이후 실행
        doLast {
            val specFile = layout.buildDirectory.file("api-spec/openapi3.json").get().asFile
            processOpenApiSpec(specFile)

            // 로컬 개발 시 Swagger UI에서 즉시 확인할 수 있도록 build 폴더뿐만 아니라
            // src/main/resources/static/docs 에도 생성된 문서를 복사합니다.
            val srcDocsDir = layout.projectDirectory.dir("src/main/resources/static/docs").asFile
            srcDocsDir.mkdirs()
            specFile.copyTo(File(srcDocsDir, "openapi3.json"), overwrite = true)
        }
    }

    // 2. 생성된 OpenAPI Spec 파일을 정적 리소스 경로로 복사하는 태스크
    val copyOpenApiSpec = tasks.register<Copy>("copyOpenApiSpec") {
        dependsOn("injectOpenApiSecurity")
        from(layout.buildDirectory.dir("api-spec"))
        into(layout.buildDirectory.dir("resources/main/static/docs"))
    }

    // 3. 빌드 시 문서가 포함되도록 jar 및 bootJar 태스크의 의존성을 설정합니다.
    tasks.named("bootJar") {
        dependsOn(copyOpenApiSpec)
    }

    tasks.named("jar") {
        dependsOn(copyOpenApiSpec)
    }

    // 4. 실행 클래스 확인 태스크 이전에 문서 복사가 완료되도록 순서를 보장합니다.
    tasks.named("resolveMainClassName") {
        mustRunAfter(copyOpenApiSpec)
    }

    // 5. clean 태스크 시 생성된 문서 디렉토리도 함께 삭제하도록 설정합니다.
    tasks.named<org.gradle.api.tasks.Delete>("clean") {
        delete(layout.buildDirectory.dir("generated-docs"))
    }
}

/**
 * OpenAPI Spec JSON 파일을 읽어 필요한 내용을 수정/추가하는 핵심 로직입니다.
 */
fun processOpenApiSpec(specFile: File) {
    val jsonSlurper = JsonSlurper()

    @Suppress("UNCHECKED_CAST")
    val json = jsonSlurper.parseText(specFile.readText()) as MutableMap<String, Any?>

    // [1] Security Schemes 설정: bearerAuth(JWT) 방식을 전역 컴포넌트에 추가
    @Suppress("UNCHECKED_CAST")
    val components = json.getOrPut("components") { mutableMapOf<String, Any?>() } as MutableMap<String, Any?>
    
    @Suppress("UNCHECKED_CAST")
    val securitySchemes = components.getOrPut("securitySchemes") { mutableMapOf<String, Any?>() } as MutableMap<String, Any?>
    
    securitySchemes["bearerAuth"] = mapOf(
        "type" to "http",
        "scheme" to "bearer",
        "bearerFormat" to "JWT",
        "description" to "카카오 로그인 후 발급받은 JWT 토큰을 입력해주세요."
    )

    // [2] 자주 사용되는 파라미터의 예시 값(Example) 정의
    // Swagger UI에서 'Try it out' 클릭 시 자동으로 채워질 기본값들입니다.
    val paramExamples = mapOf(
        "mapLat" to "37.5165", "mapLng" to "127.1040", "scope" to "1000",
        "userLat" to "37.516", "userLng" to "127.104", "minLength" to "0",
        "maxLength" to "10000", "page" to "0", "id" to "689c3143182cecc6353cca7b",
        "lat" to "37.5165", "lng" to "127.1040", "startLat" to "37.5165",
        "startLng" to "127.1040", "courseIds" to "689c3143182cecc6353cca7b,689c3143182cecc6353cca7c",
        "courseId" to "689c3143182cecc6353cca7b", "reviewId" to "679c1234562cecc6394cca7b"
    )

    /**
     * JSON 문자열로 되어 있는 예시 데이터(examples)를 실제 JSON 객체 구조로 변환하는 헬퍼 함수입니다.
     * 이를 통해 Swagger UI에서 예시 데이터가 문자열이 아닌 구조화된 JSON으로 예쁘게 표시됩니다.
     */
    fun parseExamples(contentObj: Any?) {
        val content = contentObj as? Map<*, *> ?: return
        @Suppress("UNCHECKED_CAST")
        val appJson = content["application/json"] as? MutableMap<String, Any?> ?: return
        @Suppress("UNCHECKED_CAST")
        val examples = appJson["examples"] as? MutableMap<String, Any?> ?: return

        examples.values.filterIsInstance<MutableMap<String, Any?>>().forEach exLoop@{ exampleObj ->
            val valueStr = (exampleObj["value"] as? String)?.trim() ?: return@exLoop
            if (valueStr.startsWith("{") || valueStr.startsWith("[")) {
                try {
                    exampleObj["value"] = jsonSlurper.parseText(valueStr)
                } catch (ignore: Exception) {}
            }
        }
    }

    // [3] 모든 API 경로(paths)를 순회하며 추가 정보 주입
    val paths = json["paths"] as? Map<*, *> ?: emptyMap<String, Any>()
    
    paths.values.filterIsInstance<Map<*, *>>()
        .flatMap { it.values.filterIsInstance<MutableMap<String, Any?>>() }
        .forEach { op ->
            // A. '로그인 필요' 문구가 포함된 API에 보안 요구사항(bearerAuth) 자동 추가
            if ((op["description"] as? String)?.contains("로그인 필요") == true) {
                op["security"] = listOf(mapOf("bearerAuth" to emptyList<String>()))
            }
            
            // B. 정의된 파라미터가 있다면 미리 준비한 예시 값을 주입
            val params = op["parameters"] as? List<*> ?: emptyList<Any>()
            params.filterIsInstance<MutableMap<String, Any?>>().forEach { param ->
                paramExamples[param["name"] as? String]?.let { param["example"] = it }
            }

            // C. 응답(responses) 섹션 처리: 상태 코드별 설명 보완 및 예외 메시지 상세화
            val responses = op["responses"] as? Map<*, *> ?: emptyMap<String, Any>()
            responses.entries.forEach resLoop@{ (statusCode, responseObj) ->
                @Suppress("UNCHECKED_CAST")
                val response = responseObj as? MutableMap<String, Any?> ?: return@resLoop

                // HTTP 상태 코드에 맞는 한국어 설명 추가
                val desc = response["description"]
                if (desc == statusCode.toString() || desc == "OK") {
                    response["description"] = when (statusCode.toString()) {
                        "200" -> "성공 (OK)"
                        "201" -> "생성됨 (Created)"
                        "204" -> "내용 없음 (No Content)"
                        "400" -> "잘못된 요청 (Bad Request)"
                        "401" -> "인증 실패 (Unauthorized)"
                        "403" -> "접근 권한 없음 (Forbidden)"
                        "404" -> "리소스를 찾을 수 없음 (Not Found)"
                        "409" -> "리소스 충돌 (Conflict)"
                        "500" -> "서버 내부 오류 (Internal Server Error)"
                        else -> desc
                    }
                }

                // 응답 본문 예시 파싱 (문자열 -> JSON 객체)
                parseExamples(response["content"])

                // 4xx, 5xx 에러 응답의 경우, RestDocs에서 정의한 여러 예시들의 message 필드를 모아
                // API 설명(description) 부분에 '발생 가능한 예외 상황'으로 목록화해줍니다.
                if (statusCode.toString() !in listOf("200", "201", "204")) {
                    val content = response["content"] as? Map<*, *>
                    val appJson = content?.get("application/json") as? Map<*, *>
                    val examples = appJson?.get("examples") as? Map<*, *>
                    
                    val errorMessages = mutableListOf<String>()
                    examples?.values?.filterIsInstance<Map<*, *>>()?.forEach { exampleObj ->
                        val value = exampleObj["value"] as? Map<*, *>
                        val messageStr = value?.get("message") as? String
                        if (messageStr != null) {
                            errorMessages.add(messageStr)
                        }
                    }

                    // 400 에러에는 기본적으로 파라미터 오류 설명을 추가
                    if (statusCode.toString() == "400") {
                        errorMessages.add("요청 파라미터가 잘못된 경우 (예: 필수값 누락, null 등)")
                    }

                    if (errorMessages.isNotEmpty()) {
                        val originalDesc = response["description"] as? String ?: ""
                        var newDesc = originalDesc + "\n\n**[발생 가능한 예외 상황]**\n"
                        errorMessages.distinct().forEach { msg ->
                            newDesc += "- $msg\n"
                        }
                        response["description"] = newDesc
                    }
                }
            }

            // D. 요청 본문(requestBody)이 있는 경우 예시 파싱
            val requestBody = op["requestBody"] as? Map<*, *> ?: emptyMap<String, Any>()
            parseExamples(requestBody["content"])
        }

    // [4] Multipart 파일 업로드 API의 requestBody 자동 주입
    // restdocs-api-spec이 requestParts를 OpenAPI requestBody로 변환하지 못하는 한계를 보완합니다.
    // build/generated-snippets 하위의 request-parts.adoc 파일을 스캔하여 자동으로 주입합니다.
    val snippetsDir = layout.buildDirectory.dir("generated-snippets").get().asFile
    if (snippetsDir.exists()) {
        // operationId → multipart part 목록 매핑 생성
        val multipartParts = mutableMapOf<String, List<Pair<String, String>>>() // operationId → [(name, description)]

        snippetsDir.listFiles()?.filter { it.isDirectory }?.forEach { snippetDir ->
            val requestPartsFile = File(snippetDir, "request-parts.adoc")
            val resourceFile = File(snippetDir, "resource.json")
            if (requestPartsFile.exists() && resourceFile.exists()) {
                // resource.json에서 operationId 추출
                @Suppress("UNCHECKED_CAST")
                val resource = jsonSlurper.parseText(resourceFile.readText()) as Map<String, Any?>
                val operationId = resource["operationId"] as? String ?: return@forEach

                // request-parts.adoc 파싱: |`+partName+`\n|설명 형식
                val parts = mutableListOf<Pair<String, String>>()
                val lines = requestPartsFile.readLines()
                var i = 0
                while (i < lines.size) {
                    val partMatch = Regex("""\|`\+(.+?)\+`""").find(lines[i])
                    if (partMatch != null && i + 1 < lines.size) {
                        val partName = partMatch.groupValues[1]
                        val desc = lines[i + 1].removePrefix("|").trim()
                        parts.add(partName to desc)
                    }
                    i++
                }
                if (parts.isNotEmpty()) {
                    multipartParts[operationId] = parts
                }
            }
        }

        // OpenAPI spec의 각 operation에 multipart requestBody 주입
        if (multipartParts.isNotEmpty()) {
            paths.values.filterIsInstance<Map<*, *>>()
                .flatMap { it.values.filterIsInstance<MutableMap<String, Any?>>() }
                .forEach { op ->
                    val opId = op["operationId"] as? String ?: return@forEach
                    val parts = multipartParts[opId] ?: return@forEach

                    val properties = mutableMapOf<String, Any>()
                    val required = mutableListOf<String>()
                    parts.forEach { (name, desc) ->
                        properties[name] = mutableMapOf(
                            "type" to "string",
                            "format" to "binary",
                            "description" to desc
                        )
                        required.add(name)
                    }

                    op["requestBody"] = mutableMapOf(
                        "content" to mutableMapOf(
                            "multipart/form-data" to mutableMapOf(
                                "schema" to mutableMapOf(
                                    "type" to "object",
                                    "properties" to properties,
                                    "required" to required
                                )
                            )
                        )
                    )
                }
        }
    }

    // 최종 수정된 내용을 예쁘게(pretty print) 하여 파일로 저장
    specFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(json)))
}
