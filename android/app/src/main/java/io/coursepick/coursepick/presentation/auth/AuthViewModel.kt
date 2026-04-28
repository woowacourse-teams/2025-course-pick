package io.coursepick.coursepick.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialAuthenticator
import io.coursepick.coursepick.domain.auth.SocialToken
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<AuthUiEvent>()
        val uiEvent: SharedFlow<AuthUiEvent> get() = _uiEvent.asSharedFlow()

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
                            _uiEvent.emit(AuthUiEvent.AuthenticateSuccess)
                        }.onFailure {
                            _uiEvent.emit(AuthUiEvent.AuthenticateFailure)
                        }
                    }
                },
                onFailure = {
                    viewModelScope.launch {
                        _uiEvent.emit(AuthUiEvent.AuthenticateFailure)
                    }
                },
            )
        }
    }
