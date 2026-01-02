package io.coursepick.coursepick.presentation.auth

import androidx.lifecycle.ViewModel
import io.coursepick.coursepick.domain.auth.SocialAuthenticator

class AuthViewModel(
    private val socialAuthenticator: SocialAuthenticator,
) : ViewModel() {
    fun authenticate() {
        socialAuthenticator.authenticate(
            onSuccess = { socialToken: String ->
                // 성공시, 서버 api 호출
            },
            onFailure = { error: Throwable ->
                // 실패 이벤트 발생
            },
        )
    }
}
