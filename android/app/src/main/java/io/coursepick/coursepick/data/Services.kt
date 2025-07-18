package io.coursepick.coursepick.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.coursepick.coursepick.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object Services {
    private val client = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor()).build()

    private val contentType = "application/json".toMediaType()

    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()

    val courseService: CourseService = retrofit.create(CourseService::class.java)
}
