package io.coursepick.coursepick.data.interceptor

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor

class PrettyPrintLogger : HttpLoggingInterceptor.Logger {
    private val json = Json { prettyPrint = true }

    override fun log(message: String) {
        val formattedMessage: String = message.toPrettyJsonStringOrThis()
        Platform.get().log(formattedMessage)
    }

    private fun String.toPrettyJsonStringOrThis(): String =
        runCatching {
            val element: JsonElement = json.parseToJsonElement(this)
            json.encodeToString(JsonElement.serializer(), element)
        }.getOrElse { this }
}
