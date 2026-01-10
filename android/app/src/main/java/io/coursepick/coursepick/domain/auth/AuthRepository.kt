package io.coursepick.coursepick.domain.auth

interface AuthRepository {
    suspend fun sign(
        socialType: String,
        socialToken: SocialToken,
    ): String
}
