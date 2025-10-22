package io.coursepick.coursepick.data.interceptor

import io.coursepick.coursepick.data.NetworkMonitor
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class OffLineInterceptor(
    private val networkMonitor: NetworkMonitor,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!networkMonitor.isConnected()) {
            throw IOException("인터넷 없음")
        }
        return chain.proceed(chain.request())
    }
}
