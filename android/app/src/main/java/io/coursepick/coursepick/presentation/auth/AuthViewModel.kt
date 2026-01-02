package io.coursepick.coursepick.presentation.auth

import androidx.lifecycle.ViewModel
import io.coursepick.coursepick.domain.auth.SocialAuthenticator
import io.coursepick.coursepick.presentation.ui.MutableSingleLiveData
import io.coursepick.coursepick.presentation.ui.SingleLiveData

class AuthViewModel(
    private val socialAuthenticator: SocialAuthenticator,
) : ViewModel() {
    private val _event: MutableSingleLiveData<AuthUiEvent> = MutableSingleLiveData()
    val event: SingleLiveData<AuthUiEvent> get() = _event

    fun authenticate() {
        socialAuthenticator.authenticate(
            onSuccess = { socialToken: String ->
                // TODO: 서버 api 호출
            },
            onFailure = { error: Throwable ->
                _event.value = AuthUiEvent.AuthenticateFailure
            },
        )
    }
}
