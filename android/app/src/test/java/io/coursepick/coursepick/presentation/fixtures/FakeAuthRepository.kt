package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialToken

class FakeAuthRepository : AuthRepository {
    private var userId: String? = null
    override var cachedAccessToken: String? = null

    override suspend fun sign(
        socialType: String,
        socialToken: SocialToken,
    ): String {
        userId = USER_ID
        return ACCESS_TOKEN
    }

    override suspend fun userId(): String? = userId

    override suspend fun saveAccessToken(token: String) {
        cachedAccessToken = token
    }

    override suspend fun preloadAccessToken() {
        accessToken()
    }

    override suspend fun accessToken(): String? {
        if (cachedAccessToken == null) {
            cachedAccessToken = ACCESS_TOKEN
        }
        return cachedAccessToken
    }

    override suspend fun clearCredentials() {
        userId = null
        cachedAccessToken = null
    }

    companion object {
        private const val USER_ID = "user_id"
        private const val ACCESS_TOKEN = "access_token"
    }
}
