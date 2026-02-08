package io.coursepick.coursepick.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialAuthenticator
import io.coursepick.coursepick.domain.auth.SocialToken
import io.coursepick.coursepick.presentation.ui.MutableSingleLiveData
import io.coursepick.coursepick.presentation.ui.SingleLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _event: MutableSingleLiveData<AuthUiEvent> = MutableSingleLiveData()
        val event: SingleLiveData<AuthUiEvent> get() = _event

        fun authenticate(authenticator: SocialAuthenticator) {
            authenticator.authenticate(
                onSuccess = { socialAccessToken: String ->
                    viewModelScope.launch {
                        runCatching {
                            authRepository.sign(
                                authenticator.socialType,
                                SocialToken(socialAccessToken),
                            )
                        }.onSuccess { token: String ->
                            authRepository.saveAccessToken(token)
                            _event.value = AuthUiEvent.AuthenticateSuccess
                        }.onFailure {
                            _event.value = AuthUiEvent.AuthenticateFailure
                        }
                    }
                },
                onFailure = { error: Throwable ->
                    _event.value = AuthUiEvent.AuthenticateFailure
                },
            )
        }
    }
