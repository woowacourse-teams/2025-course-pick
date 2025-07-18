package io.coursepick.coursepick.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.coursepick.coursepick.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit

object Services {
    private val client: OkHttpClient = client()
    private val converterFactory: Converter.Factory =
        Json.asConverterFactory("application/json".toMediaType())
    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()

    val courseService: CourseService = retrofit.create(CourseService::class.java)

    private fun client(): OkHttpClient {
        val isDebugBuild: Boolean = BuildConfig.DEBUG
        val loggingLevel =
            if (isDebugBuild) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        val interceptor = HttpLoggingInterceptor().setLevel(loggingLevel)
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }
}
