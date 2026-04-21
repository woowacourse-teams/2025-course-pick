package io.coursepick.coursepick.data.interceptor

import io.coursepick.coursepick.domain.auth.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class SignInterceptor
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val requestBuilder: Request.Builder = chain.request().newBuilder()
            runBlocking {
                authRepository.accessToken()?.let { accessToken: String ->
                    requestBuilder.addHeader("Authorization", "Bearer $accessToken")
                }
            }
            return chain.proceed(requestBuilder.build())
        }
    }
