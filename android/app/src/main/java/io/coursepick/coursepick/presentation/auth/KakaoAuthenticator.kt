package io.coursepick.coursepick.presentation.auth

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import io.coursepick.coursepick.domain.auth.SocialAuthenticator
import timber.log.Timber

class KakaoAuthenticator(
    private val context: Context,
) : SocialAuthenticator {
    private val client = UserApiClient.instance

    override fun authenticate(
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        val kakaoTalkLoginAvailable: Boolean =
            client.isKakaoTalkLoginAvailable(context)

        if (kakaoTalkLoginAvailable) {
            client.loginWithKakaoTalk(
                context = context,
                callback = kakaoTalkLoginCallback(onSuccess, onFailure),
            )
        } else {
            client.loginWithKakaoAccount(
                context = context,
                callback = kakaoAccountLoginCallback(onSuccess, onFailure),
            )
        }
    }

    private fun kakaoTalkLoginCallback(
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit,
    ): (OAuthToken?, Throwable?) -> Unit =
        { token: OAuthToken?, error: Throwable? ->
            when {
                error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                    Timber.e("카카오톡 로그인 취소")
                    onFailure(error)
                }

                error != null -> {
                    Timber.e("카카오톡 로그인 실패 $error")
                    client.loginWithKakaoAccount(
                        context = context,
                        callback = kakaoAccountLoginCallback(onSuccess, onFailure),
                    )
                }

                token != null -> {
                    Timber.d("카카오톡 로그인 성공")
                    onSuccess(token.accessToken)
                }
            }
        }

    private fun kakaoAccountLoginCallback(
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit,
    ): (OAuthToken?, Throwable?) -> Unit =
        { token: OAuthToken?, error: Throwable? ->
            when {
                error != null -> {
                    Timber.e("카카오계정으로 로그인 실패 $error")
                    onFailure(error)
                }

                token != null -> {
                    Timber.d("카카오계정으로 로그인 성공")
                    onSuccess(token.accessToken)
                }
            }
        }
}
