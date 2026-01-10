package io.coursepick.coursepick.data.auth

import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialToken
import javax.inject.Inject

class DefaultAuthRepository
    @Inject
    constructor(
        private val tokenLocalDataSource: TokenLocalDataSource,
        private val service: SignService,
    ) : AuthRepository {
        override suspend fun sign(
            socialType: String,
            socialToken: SocialToken,
        ): String {
            val token = TokenDto(socialToken.accessToken)
            return service.sign(socialType, token).accessToken
        }

        override suspend fun saveAccessToken(token: String) {
            tokenLocalDataSource.saveAccessToken(token)
        }

        override suspend fun accessToken(): String? = tokenLocalDataSource.accessToken()

        override suspend fun clearAccessToken() = tokenLocalDataSource.clearAccessToken()
    }
