package io.coursepick.coursepick.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.coursepick.coursepick.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object Services {
    private val loggingLevel =
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    private val loggingInterceptor =
        HttpLoggingInterceptor(PrettyPrintLogger()).setLevel(loggingLevel)

    private val client: OkHttpClient =
        OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

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

    val courseService: CourseService = retrofit.create(CourseService::class.java)

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

    val searchService: SearchService =
        kakaoRetrofit.create(SearchService::class.java)
}
