package io.coursepick.coursepick.data.auth

import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialToken

class DefaultAuthRepository(
    val service: SignService,
) : AuthRepository {
    override suspend fun sign(
        socialType: String,
        socialToken: SocialToken,
    ): String {
        val token = TokenDto(socialToken.accessToken)
        return service.sign(socialType, token).accessToken
    }
}
