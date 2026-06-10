package io.coursepick.coursepick.data.auth

import io.coursepick.coursepick.domain.Outcome
import io.coursepick.coursepick.domain.auth.AccessToken
import io.coursepick.coursepick.domain.auth.AccountType
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.AuthenticationError
import io.coursepick.coursepick.domain.auth.Authenticator
import io.coursepick.coursepick.presentation.Logger
import javax.inject.Inject

class DefaultAuthRepository
    @Inject
    constructor(
        private val dataSource: TokenLocalDataSource,
        private val service: SignService,
    ) : AuthRepository {
        override var cachedAccessToken: AccessToken? = null
            private set

        override suspend fun signIn(authenticator: Authenticator): Outcome<Unit, AuthenticationError> =
            when (val outcome: Outcome<AccessToken, AuthenticationError> = authenticator.authenticate()) {
                is Outcome.Success -> {
                    runCatching {
                        val signResponse: SignResponseDto = service.sign(authenticator.accountType.endPoint(), TokenDto(outcome.data.data))
                        cachedAccessToken = AccessToken(signResponse.accessToken)
                        dataSource.saveAccessToken(signResponse.accessToken)
                        dataSource.saveUserId(signResponse.userId)
                        Logger.log(Logger.Event.Success("coursepick_sign_in"))
                        Outcome.Success(Unit)
                    }.getOrElse { throwable: Throwable ->
                        Logger.log(Logger.Event.Failure("coursepick_sign_in"), "message" to throwable.message.orEmpty())
                        Outcome.Failure(AuthenticationError.Unknown)
                    }
                }

                is Outcome.Failure -> {
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
