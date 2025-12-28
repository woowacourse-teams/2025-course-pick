package io.coursepick.coursepick.domain.auth

interface SocialAuthenticator {
    /**
     * @return social accessToken을 반환한다.
     * */
    fun authenticate(
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit,
    )
}
