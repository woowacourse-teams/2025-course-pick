package io.coursepick.coursepick.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import io.coursepick.coursepick.domain.auth.SocialAuthenticator
import io.coursepick.coursepick.presentation.auth.KakaoAuthenticator

@Module
@InstallIn(ActivityComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindSocialAuthenticator(implementation: KakaoAuthenticator): SocialAuthenticator
}
