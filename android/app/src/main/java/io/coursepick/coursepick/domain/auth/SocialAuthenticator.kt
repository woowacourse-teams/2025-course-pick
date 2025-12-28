package io.coursepick.coursepick.domain.auth

interface SocialAuthenticator {
    fun authenticate(): Boolean
}
