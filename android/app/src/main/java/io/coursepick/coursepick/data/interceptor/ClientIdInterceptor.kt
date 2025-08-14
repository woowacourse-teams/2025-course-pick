package io.coursepick.coursepick.data.interceptor

import io.coursepick.coursepick.presentation.InstallationId
import okhttp3.Interceptor
import okhttp3.Response

class ClientIdInterceptor(
    private val installationId: InstallationId,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain
                .request()
                .newBuilder()
                .header("X-Client-Id", installationId.value)
                .build()
        return chain.proceed(request)
    }
}
