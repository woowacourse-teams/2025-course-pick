package io.coursepick.coursepick.data.auth

import io.coursepick.coursepick.domain.Outcome
import io.coursepick.coursepick.domain.auth.AccessToken
import io.coursepick.coursepick.domain.auth.AccountType
import io.coursepick.coursepick.domain.auth.AuthRepository2
import io.coursepick.coursepick.domain.auth.AuthenticationError
import io.coursepick.coursepick.domain.auth.Authenticator
import javax.inject.Inject

class DefaultAuthRepository2
    @Inject
    constructor(
        private val dataSource: TokenLocalDataSource,
        private val service: SignService,
    ) : AuthRepository2 {
        override var cachedAccessToken: AccessToken? = null
            private set

        override suspend fun signIn(authenticator: Authenticator): Outcome<Unit, AuthenticationError> =
            when (val outcome: Outcome<AccessToken, AuthenticationError> = authenticator.authenticate()) {
                is Outcome.Success<AccessToken> -> {
                    runCatching {
                        val signResponse: SignResponseDto = service.sign(authenticator.accountType.endPoint(), TokenDto(outcome.data.data))
                        cachedAccessToken = outcome.data
                        dataSource.saveAccessToken(signResponse.accessToken)
                        dataSource.saveUserId(signResponse.userId)
                        Outcome.Success(Unit)
                    }.getOrElse {
                        Outcome.Failure(AuthenticationError.Unknown)
                    }
                }

                is Outcome.Failure<AuthenticationError> -> {
                    outcome
                }
            }

        override suspend fun userId(): String? = dataSource.userId()

        override suspend fun accessToken(): AccessToken? {
            if (cachedAccessToken == null) {
                cachedAccessToken = dataSource.accessToken()?.let(::AccessToken)
            }
            return cachedAccessToken
        }

        private fun AccountType.endPoint(): String =
            when (this) {
                AccountType.KAKAO -> "kakao"
            }
    }
