import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.tasks.Copy
import java.io.File

val isProdProfile = project.findProperty("profile") == "prod"

if (!isProdProfile) {
    tasks.register("injectOpenApiSecurity") {
        dependsOn("openapi3")
        doLast {
            val specFile = layout.buildDirectory.file("api-spec/openapi3.json").get().asFile
            processOpenApiSpec(specFile)
        }
    }

    // 문서 복사 태스크 (테스트 결과를 기반으로 생성된 문서를 복사)
    val copyOpenApiSpec = tasks.register<Copy>("copyOpenApiSpec") {
        dependsOn("injectOpenApiSecurity")
        from(layout.buildDirectory.dir("api-spec"))
        into(layout.buildDirectory.dir("resources/main/static/docs"))
    }

    // jar를 묶기 직전에(문서를 포함시킴) 실행하도록 의존성 주입
    tasks.named("bootJar") {
        dependsOn(copyOpenApiSpec)
    }

    tasks.named("jar") {
        dependsOn(copyOpenApiSpec)
    }

    // 순서만 보장하기 위해 mustRunAfter 사용
    tasks.named("resolveMainClassName") {
        mustRunAfter(copyOpenApiSpec)
    }

    tasks.named<org.gradle.api.tasks.Delete>("clean") {
        delete(layout.buildDirectory.dir("generated-docs"))
    }
}

fun processOpenApiSpec(specFile: File) {
    val jsonSlurper = JsonSlurper()

    @Suppress("UNCHECKED_CAST")
    val json = jsonSlurper.parseText(specFile.readText()) as MutableMap<String, Any?>

    // 1. components.securitySchemes에 bearerAuth 추가
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

    // 2. 파라미터 예시 값 정의
    val paramExamples = mapOf(
        "mapLat" to "37.5165", "mapLng" to "127.1040", "scope" to "1000",
        "userLat" to "37.516", "userLng" to "127.104", "minLength" to "0",
        "maxLength" to "10000", "page" to "0", "id" to "689c3143182cecc6353cca7b",
        "lat" to "37.5165", "lng" to "127.1040", "startLat" to "37.5165",
        "startLng" to "127.1040", "courseIds" to "689c3143182cecc6353cca7b,689c3143182cecc6353cca7c",
        "courseId" to "689c3143182cecc6353cca7b", "reviewId" to "679c1234562cecc6394cca7b"
    )

    // 반복되는 예시 문자열 파싱 헬퍼 함수
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

    // 3. 로그인 필요한 API에 security 추가 + 파라미터 example 주입 + 응답/요청 JSON 객체 변환
    val paths = json["paths"] as? Map<*, *> ?: emptyMap<String, Any>()
    
    paths.values.filterIsInstance<Map<*, *>>()
        .flatMap { it.values.filterIsInstance<MutableMap<String, Any?>>() }
        .forEach { op ->
            // Security 추가
            if ((op["description"] as? String)?.contains("로그인 필요") == true) {
                op["security"] = listOf(mapOf("bearerAuth" to emptyList<String>()))
            }
            
            // 파라미터 Example 주입
            val params = op["parameters"] as? List<*> ?: emptyList<Any>()
            params.filterIsInstance<MutableMap<String, Any?>>().forEach { param ->
                paramExamples[param["name"] as? String]?.let { param["example"] = it }
            }

            // 응답 처리 (상태 코드 설명 + 예시 파싱)
            val responses = op["responses"] as? Map<*, *> ?: emptyMap<String, Any>()
            responses.entries.forEach resLoop@{ (statusCode, responseObj) ->
                @Suppress("UNCHECKED_CAST")
                val response = responseObj as? MutableMap<String, Any?> ?: return@resLoop

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
                parseExamples(response["content"])
            }

            // 요청 예시 파싱
            val requestBody = op["requestBody"] as? Map<*, *> ?: emptyMap<String, Any>()
            parseExamples(requestBody["content"])
        }

    specFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(json)))
}
