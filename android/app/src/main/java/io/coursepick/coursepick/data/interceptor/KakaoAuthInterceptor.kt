package io.coursepick.coursepick.data.interceptor

import io.coursepick.coursepick.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

object KakaoAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
                .build()
        return chain.proceed(request)
    }
}
