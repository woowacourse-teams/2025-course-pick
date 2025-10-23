package io.coursepick.coursepick.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.coursepick.coursepick.BuildConfig
import io.coursepick.coursepick.data.DefaultNetworkMonitor
import io.coursepick.coursepick.data.NetworkMonitor
import io.coursepick.coursepick.data.interceptor.ClientIdInterceptor
import io.coursepick.coursepick.data.interceptor.OffLineInterceptor
import io.coursepick.coursepick.data.interceptor.PrettyPrintLogger
import io.coursepick.coursepick.presentation.InstallationId
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingLevel =
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        return HttpLoggingInterceptor(PrettyPrintLogger()).setLevel(loggingLevel)
    }

    @Provides
    fun provideNetworkMonitor(
        @ApplicationContext applicationContext: Context,
    ): NetworkMonitor = DefaultNetworkMonitor(applicationContext)

    @Provides
    fun provideRetrofit(
        loggingInterceptor: HttpLoggingInterceptor,
        networkMonitor: NetworkMonitor,
        installationId: InstallationId,
    ): Retrofit {
        val client: OkHttpClient =
            OkHttpClient
                .Builder()
                .addInterceptor(ClientIdInterceptor(installationId))
                .addInterceptor(loggingInterceptor)
                .addInterceptor(OffLineInterceptor(networkMonitor))
                .build()
        val json =
            Json {
                ignoreUnknownKeys = true
            }

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
