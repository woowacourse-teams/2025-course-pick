package io.coursepick.coursepick.data.auth

import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class DefaultAuthRepository
    @Inject
    constructor(
        private val tokenLocalDataSource: TokenLocalDataSource,
        private val service: SignService,
    ) : AuthRepository {
        private val _cachedAccessToken = MutableStateFlow<String?>(null)

        override val cachedAccessToken: Flow<String?> = _cachedAccessToken.asStateFlow()

        override suspend fun sign(
            socialType: String,
            socialToken: SocialToken,
        ): String {
            val token = TokenDto(socialToken.accessToken)
            return service.sign(socialType, token).accessToken
        }

        override suspend fun saveAccessToken(token: String) {
            _cachedAccessToken.update { token }
            tokenLocalDataSource.saveAccessToken(token)
        }

        override suspend fun preloadAccessToken() {
            accessToken()
        }

        override suspend fun accessToken(): String? {
            if (_cachedAccessToken.value == null) {
                val localToken = tokenLocalDataSource.accessToken()
                _cachedAccessToken.update { localToken }
            }
            return _cachedAccessToken.value
        }

        override suspend fun clearAccessToken() {
            _cachedAccessToken.update { null }
            tokenLocalDataSource.clearAccessToken()
        }
    }
