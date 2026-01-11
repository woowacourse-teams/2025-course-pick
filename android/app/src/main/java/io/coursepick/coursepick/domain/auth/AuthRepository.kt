package io.coursepick.coursepick.domain.auth

interface AuthRepository {
    suspend fun sign(
        socialType: String,
        socialToken: SocialToken,
    ): String

    suspend fun saveAccessToken(token: String)

    suspend fun accessToken(): String?

    suspend fun clearAccessToken()
}
