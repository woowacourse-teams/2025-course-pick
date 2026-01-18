package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialToken

class FakeAuthRepository : AuthRepository {
    override suspend fun sign(
        socialType: String,
        socialToken: SocialToken,
    ): String = "token 123456"

    override suspend fun saveAccessToken(token: String) = Unit

    override suspend fun accessToken(): String? = null

    override suspend fun clearAccessToken() = Unit
}
