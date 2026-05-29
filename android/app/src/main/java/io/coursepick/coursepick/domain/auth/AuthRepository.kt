package io.coursepick.coursepick.domain.auth

interface AuthRepository {
    val cachedAccessToken: String?

    suspend fun sign(
        socialType: String,
        socialToken: SocialToken,
    ): String

    suspend fun userId(): String?

    suspend fun saveAccessToken(token: String)

    suspend fun preloadAccessToken()

    suspend fun accessToken(): String?

    suspend fun clearAccessToken()
}
