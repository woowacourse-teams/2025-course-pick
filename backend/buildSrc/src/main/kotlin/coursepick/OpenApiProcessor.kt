package coursepick

import java.io.File

object OpenApiProcessor {
    fun process(specFile: File) {
        val jsonSlurper = groovy.json.JsonSlurper()
        val json = jsonSlurper.parseText(specFile.readText()) as MutableMap<String, Any?>

        // 1. components.securitySchemes에 bearerAuth 추가
        val components =
            json.getOrPut("components") { mutableMapOf<String, Any?>() } as MutableMap<String, Any?>
        val securitySchemes =
            components.getOrPut("securitySchemes") { mutableMapOf<String, Any?>() } as MutableMap<String, Any?>
        securitySchemes["bearerAuth"] = mapOf(
            "type" to "http",
            "scheme" to "bearer",
            "bearerFormat" to "JWT",
            "description" to "카카오 로그인 후 발급받은 JWT 토큰을 입력해주세요."
        )

        // 2. 파라미터 예시 값 정의 (파라미터 이름 → 예시 값)
        val paramExamples = mapOf(
            "mapLat" to "37.5165",
            "mapLng" to "127.1040",
            "scope" to "1000",
            "userLat" to "37.516",
            "userLng" to "127.104",
            "minLength" to "0",
            "maxLength" to "10000",
            "page" to "0",
            "id" to "689c3143182cecc6353cca7b",
            "lat" to "37.5165",
            "lng" to "127.1040",
            "startLat" to "37.5165",
            "startLng" to "127.1040",
            "courseIds" to "689c3143182cecc6353cca7b,689c3143182cecc6353cca7c"
        )

        // 3. 로그인 필요한 API에 security 추가 + 파라미터 example 주입 + 응답/요청 JSON 객체 변환
        val paths = json["paths"] as? Map<*, *> ?: emptyMap<String, Any>()

        val operations = paths.values
            .filterIsInstance<Map<*, *>>()
            .flatMap { it.values.filterIsInstance<MutableMap<String, Any?>>() }

        operations.forEach { op ->
            // Security 추가
            if ((op["description"] as? String)?.contains("로그인 필요") == true) {
                op["security"] = listOf(mapOf("bearerAuth" to emptyList<String>()))
            }
            // 파라미터 Example 주입
            val params = op["parameters"] as? List<*> ?: emptyList<Any>()
            params.filterIsInstance<MutableMap<String, Any?>>().forEach { param ->
                val name = param["name"] as? String
                paramExamples[name]?.let { example ->
                    param["example"] = example
                }
            }

            // 응답 예시 문자열을 JSON 객체로 파싱하여 교체 및 설명 추가
            val responses = op["responses"] as? Map<*, *> ?: emptyMap<String, Any>()
            responses.entries.forEach { (statusCode, responseObj) ->
                val response = responseObj as? MutableMap<String, Any?> ?: return@forEach
                
                // HTTP 상태 코드에 따른 기본 설명 추가
                if (response["description"] == statusCode.toString() || response["description"] == "OK") {
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
                        else -> response["description"]
                    }
                }

                val content = response["content"] as? Map<*, *> ?: emptyMap<String, Any>()
                val applicationJson = content["application/json"] as? MutableMap<String, Any?>
                if (applicationJson != null) {
                    val examples = applicationJson["examples"] as? MutableMap<String, Any?>
                    examples?.values?.filterIsInstance<MutableMap<String, Any?>>()?.forEach { exampleObj ->
                        val valueStr = exampleObj["value"] as? String
                        if (valueStr != null && (valueStr.trim().startsWith("{") || valueStr.trim().startsWith("["))) {
                            try {
                                exampleObj["value"] = jsonSlurper.parseText(valueStr)
                            } catch (e: Exception) {
                                // 파싱 실패시 무시
                            }
                        }
                    }
                }
            }

            // 요청 예시도 동일하게 변환
            val requestBody = op["requestBody"] as? Map<*, *> ?: emptyMap<String, Any>()
            val reqContent = requestBody["content"] as? Map<*, *> ?: emptyMap<String, Any>()
            val reqApplicationJson = reqContent["application/json"] as? MutableMap<String, Any?>
            if (reqApplicationJson != null) {
                val examples = reqApplicationJson["examples"] as? MutableMap<String, Any?>
                examples?.values?.filterIsInstance<MutableMap<String, Any?>>()?.forEach { exampleObj ->
                    val valueStr = exampleObj["value"] as? String
                    if (valueStr != null && (valueStr.trim().startsWith("{") || valueStr.trim().startsWith("["))) {
                        try {
                            exampleObj["value"] = jsonSlurper.parseText(valueStr)
                        } catch (e: Exception) {
                            // 파싱 실패시 무시
                        }
                    }
                }
            }
        }

        specFile.writeText(groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(json)))
    }
}
