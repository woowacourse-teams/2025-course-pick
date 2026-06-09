package io.coursepick.coursepick.domain.auth

sealed interface AuthenticationError {
    data object Cancelled : AuthenticationError

    data object Unknown : AuthenticationError
}
