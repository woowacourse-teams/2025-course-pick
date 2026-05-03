package io.coursepick.coursepick.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import io.coursepick.coursepick.presentation.map.MapManagerFactory
import io.coursepick.coursepick.presentation.map.google.GoogleMapManagerFactory
import io.coursepick.coursepick.presentation.map.kakao.KakaoMapManagerFactory
import io.coursepick.coursepick.presentation.map.naver.NaverMapManagerFactory
import javax.inject.Qualifier

@Qualifier
annotation class KakaoMap

@Qualifier
annotation class NaverMap

@Qualifier
annotation class GoogleMap

@Module
@InstallIn(ActivityComponent::class)
abstract class MapModule {
    @Binds
    @KakaoMap
    @ActivityScoped
    abstract fun bindKakaoMapManagerFactory(implementation: KakaoMapManagerFactory): MapManagerFactory

    @Binds
    @NaverMap
    @ActivityScoped
    abstract fun bindNaverMapManagerFactory(implementation: NaverMapManagerFactory): MapManagerFactory

    @Binds
    @GoogleMap
    @ActivityScoped
    abstract fun bindGoogleMapManagerFactory(implementation: GoogleMapManagerFactory): MapManagerFactory
}
