package io.coursepick.coursepick.domain.auth

import io.coursepick.coursepick.domain.Outcome

interface Authenticator {
    val accountType: AccountType

    suspend fun authenticate(): Outcome<AccessToken, AuthenticationError>
}
