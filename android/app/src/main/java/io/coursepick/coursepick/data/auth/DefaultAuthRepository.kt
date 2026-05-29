package io.coursepick.coursepick.data.auth

import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialToken
import javax.inject.Inject
import kotlin.concurrent.Volatile

class DefaultAuthRepository
    @Inject
    constructor(
        private val tokenLocalDataSource: TokenLocalDataSource,
        private val service: SignService,
    ) : AuthRepository {
        @Volatile
        override var cachedAccessToken: String? = null
            private set

        override suspend fun sign(
            socialType: String,
            socialToken: SocialToken,
        ): String {
            val token = TokenDto(socialToken.accessToken)
            val signResponse: SignResponseDto = service.sign(socialType, token)
            tokenLocalDataSource.saveUserId(signResponse.userId)
            return signResponse.accessToken
        }

        override suspend fun userId(): String? = tokenLocalDataSource.userId()

        override suspend fun saveAccessToken(token: String) {
            cachedAccessToken = token
            tokenLocalDataSource.saveAccessToken(token)
        }

        override suspend fun preloadAccessToken() {
            accessToken()
        }

        override suspend fun accessToken(): String? {
            if (cachedAccessToken == null) {
                cachedAccessToken = tokenLocalDataSource.accessToken()
            }
            return cachedAccessToken
        }

        override suspend fun clearAccessToken() {
            tokenLocalDataSource.clearAccessToken()
            cachedAccessToken = null
        }
    }
