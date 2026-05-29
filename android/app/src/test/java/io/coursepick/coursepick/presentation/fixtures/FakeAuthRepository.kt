package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialToken

class FakeAuthRepository : AuthRepository {
    override var cachedAccessToken: String? = null

    override suspend fun sign(
        socialType: String,
        socialToken: SocialToken,
    ): String = "access_token"

    override suspend fun saveAccessToken(token: String) {
        cachedAccessToken = token
    }

    override suspend fun preloadAccessToken() {
        accessToken()
    }

    override suspend fun accessToken(): String? {
        if (cachedAccessToken == null) {
            cachedAccessToken = "access_token"
        }
        return cachedAccessToken
    }

    override suspend fun clearCredentials() {
        cachedAccessToken = null
    }
}
