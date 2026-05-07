package io.coursepick.coursepick.presentation.auth

sealed interface AuthUiEvent {
    data class AuthenticateSuccess(
        val feature: AuthFeature,
    ) : AuthUiEvent

    data object AuthenticateFailure : AuthUiEvent
}
