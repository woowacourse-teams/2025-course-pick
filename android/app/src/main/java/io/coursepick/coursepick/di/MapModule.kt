package io.coursepick.coursepick.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import io.coursepick.coursepick.presentation.map.MapManagerFactory
import io.coursepick.coursepick.presentation.map.kakao.KakaoMapManagerFactory

@Module
@InstallIn(ActivityComponent::class)
interface MapModule {
    @Binds
    @ActivityScoped
    fun bindKakaoMapManagerFactory(implementation: KakaoMapManagerFactory): MapManagerFactory
}
