package io.coursepick.coursepick.presentation.auth

import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialAuthenticator
import io.coursepick.coursepick.domain.auth.SocialToken
import io.coursepick.coursepick.presentation.extension.InstantTaskExecutorExtension
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
                socialAuthenticator =
                    object : SocialAuthenticator {
                        override fun authenticate(
                            onSuccess: (String) -> Unit,
                            onFailure: (Throwable) -> Unit,
                        ) {
                            onFailure(Throwable("소셜 로그인 실패"))
                        }
                    },
                authRepository =
                    object : AuthRepository {
                        override suspend fun sign(
                            socialType: String,
                            socialToken: SocialToken,
                        ): String = "token 123456"
                    },
            )

        // when
        viewModel.authenticate("kakao")

        // then
        assertThat(viewModel.event.value).isEqualTo(AuthUiEvent.AuthenticateFailure)
    }
}
