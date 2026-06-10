package io.coursepick.coursepick.data.auth

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import io.coursepick.coursepick.domain.Outcome
import io.coursepick.coursepick.domain.auth.AccessToken
import io.coursepick.coursepick.domain.auth.AccountType
import io.coursepick.coursepick.domain.auth.AuthenticationError
import io.coursepick.coursepick.domain.auth.Authenticator
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class KakaoAuthenticator
    @Inject
    constructor(
        private val context: Context,
    ) : Authenticator {
        override val accountType = AccountType.KAKAO

        private val client = UserApiClient.instance

        private fun loginCallback(onResult: (Outcome<AccessToken, AuthenticationError>) -> Unit): (OAuthToken?, Throwable?) -> Unit =
            { accessToken: OAuthToken?, throwable: Throwable? ->
                when {
                    accessToken != null -> {
                        onResult(Outcome.Success(AccessToken(accessToken.accessToken)))
                    }

                    throwable != null -> {
                        when {
                            throwable is ClientError && throwable.reason == ClientErrorCause.Cancelled -> {
                                onResult(Outcome.Failure(AuthenticationError.Cancelled))
                            }

                            else -> {
                                onResult(Outcome.Failure(AuthenticationError.Unknown))
                            }
                        }
                    }

                    else -> {
                        onResult(Outcome.Failure(AuthenticationError.Unknown))
                    }
                }
            }

        override suspend fun authenticate(): Outcome<AccessToken, AuthenticationError> =
            suspendCancellableCoroutine { continuation: CancellableContinuation<Outcome<AccessToken, AuthenticationError>> ->
                val isKakaoTalkLoginAvailable: Boolean = client.isKakaoTalkLoginAvailable(context)
                val loginCallback: (OAuthToken?, Throwable?) -> Unit = loginCallback(continuation::resume)

                if (isKakaoTalkLoginAvailable) {
                    client.loginWithKakaoTalk(context = context, callback = loginCallback)
                } else {
                    client.loginWithKakaoAccount(context = context, callback = loginCallback)
                }
            }
    }
