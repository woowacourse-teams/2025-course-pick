package io.coursepick.coursepick.presentation.auth

import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.SocialAuthenticator
import io.coursepick.coursepick.domain.auth.SocialToken
import io.coursepick.coursepick.presentation.extension.CoroutinesTestExtension
import io.coursepick.coursepick.presentation.extension.InstantTaskExecutorExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class AuthViewModelTest {
    @Test
    fun `소셜 로그인 실패 시 실패 이벤트가 발생한다`() {
        runTest {
            // given
            val viewModel =
                AuthViewModel(
                    authRepository =
                        object : AuthRepository {
                            override val cachedAccessToken: String? = null

                            override suspend fun sign(
                                socialType: String,
                                socialToken: SocialToken,
                            ): String = "token 123456"

                            override suspend fun saveAccessToken(token: String) = Unit

                            override suspend fun preloadAccessToken() = Unit

                            override suspend fun accessToken(): String? = null

                            override suspend fun clearAccessToken() = Unit
                        },
                )
            val uiEvents: MutableList<AuthUiEvent> = mutableListOf()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiEvent.toList(uiEvents)
            }

            // when
            viewModel.authenticate(
                object : SocialAuthenticator {
                    override val socialType: String = "fake"

                    override fun authenticate(
                        onSuccess: (String) -> Unit,
                        onFailure: (Throwable) -> Unit,
                    ) {
                        onFailure(Throwable("소셜 로그인 실패"))
                    }
                },
                AuthFeature.CustomCourse,
            )

            // then
            assertThat(uiEvents.first()).isEqualTo(AuthUiEvent.AuthenticateFailure)
        }
    }
}
