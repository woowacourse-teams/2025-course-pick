package io.coursepick.coursepick.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val cachedAccessToken: Flow<String?>

    suspend fun sign(
        socialType: String,
        socialToken: SocialToken,
    ): String

    suspend fun saveAccessToken(token: String)

    suspend fun preloadAccessToken()

    suspend fun accessToken(): String?

    suspend fun clearAccessToken()
}
