package io.coursepick.coursepick.presentation.auth

import io.coursepick.coursepick.domain.auth.SocialAuthenticator
import io.coursepick.coursepick.presentation.extension.InstantTaskExecutorExtension
import io.coursepick.coursepick.presentation.fixtures.FakeAuthRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class AuthViewModelTest {
    @Test
    fun `소셜 로그인 실패 시 실패 이벤트가 발생한다`() {
        // given
        val viewModel =
            AuthViewModel(
                authRepository = FakeAuthRepository(),
            )

        // when
        viewModel.authenticate(
            "kakao",
            object : SocialAuthenticator {
                override fun authenticate(
                    onSuccess: (String) -> Unit,
                    onFailure: (Throwable) -> Unit,
                ) {
                    onFailure(Throwable("소셜 로그인 실패"))
                }
            },
        )

        // then
        assertThat(viewModel.event.value).isEqualTo(AuthUiEvent.AuthenticateFailure)
    }
}
