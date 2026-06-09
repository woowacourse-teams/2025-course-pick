package io.coursepick.coursepick.domain.auth

import io.coursepick.coursepick.domain.Outcome

interface AuthRepository2 {
    val cachedAccessToken: AccessToken?

    suspend fun signIn(authenticator: Authenticator): Outcome<Unit, AuthenticationError>

    suspend fun accessToken(): AccessToken?
}
