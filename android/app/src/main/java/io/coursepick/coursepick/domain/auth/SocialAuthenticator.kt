package io.coursepick.coursepick.domain.auth

interface SocialAuthenticator {
    val socialType: String

    /**
     * 외부 소셜 서비스(Google, Kakao 등)를 통해 인증을 수행합니다.
     *
     * @param onSuccess 인증 성공 시 호출되는 콜백 함수입니다.
     * 인증 결과로 발급된 소셜 서비스의 액세스 토큰을 인자로 전달합니다.
     * @param onFailure 인증 실패 시 호출되는 콜백 함수입니다.
     * 인증 과정에서 발생한 에러를 인자로 전달합니다.
     */
    fun authenticate(
        onSuccess: (token: String) -> Unit,
        onFailure: (error: Throwable) -> Unit,
    )
}
