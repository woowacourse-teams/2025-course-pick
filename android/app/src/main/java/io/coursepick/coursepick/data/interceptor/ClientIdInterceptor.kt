package io.coursepick.coursepick.data.interceptor

import io.coursepick.coursepick.presentation.ClientId
import okhttp3.Interceptor
import okhttp3.Response

class ClientIdInterceptor(
    private val clientId: ClientId,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain
                .request()
                .newBuilder()
                .header("X-Client-Id", clientId.value)
                .build()
        return chain.proceed(request)
    }
}
