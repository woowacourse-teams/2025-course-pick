package io.coursepick.coursepick.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.coursepick.coursepick.BuildConfig
import io.coursepick.coursepick.data.course.CourseService
import io.coursepick.coursepick.data.interceptor.KakaoAuthInterceptor
import io.coursepick.coursepick.data.interceptor.PrettyPrintLogger
import io.coursepick.coursepick.data.search.SearchService
import io.coursepick.coursepick.presentation.ClientId
import io.coursepick.coursepick.presentation.course.ClientIdInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class Services(
    clientId: ClientId,
) {
    private val loggingLevel =
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    private val loggingInterceptor =
        HttpLoggingInterceptor(PrettyPrintLogger()).setLevel(loggingLevel)
    private val client: OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(ClientIdInterceptor(clientId))
            .addInterceptor(loggingInterceptor)
            .build()

    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    private val kakaoClient: OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(KakaoAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    private val kakaoRetrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.KAKAO_BASE_URL)
            .client(kakaoClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    val courseService: CourseService = retrofit.create(CourseService::class.java)

    val searchService: SearchService =
        kakaoRetrofit.create(SearchService::class.java)
}
