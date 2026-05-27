package io.coursepick.coursepick.presentation.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.presentation.auth.AuthFeature
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class WriteCourseReviewViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val courseRepository: CourseRepository,
    ) : ViewModel() {
        private val _event = MutableSharedFlow<UiEvent>()
        val event: SharedFlow<UiEvent> get() = _event.asSharedFlow()

        private val _reviewContent = MutableStateFlow("")
        val reviewContent: StateFlow<String> get() = _reviewContent.asStateFlow()

        private val _rating = MutableStateFlow<Float?>(null)
        val rating: StateFlow<Float?> get() = _rating.asStateFlow()

        val canSubmit: StateFlow<Boolean> =
            combine(rating, reviewContent) { rating: Float?, reviewContent: String ->
                rating != null && reviewContent.isNotBlank()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

        private val _authDialog = MutableStateFlow<AuthFeature?>(null)
        val authDialog: StateFlow<AuthFeature?> get() = _authDialog.asStateFlow()

        fun setRating(rating: Float) {
            _rating.value = rating
        }

        fun setReviewText(text: String) {
            _reviewContent.value = text.take(MAX_REVIEW_LENGTH)
        }

        fun submitReview(courseId: String) {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _authDialog.value = AuthFeature.SubmitReview(courseId)
                    return@launch
                }

                val rating: Float? = rating.value
                if (rating == null) {
                    _event.emit(UiEvent.InvalidReviewContent)
                    return@launch
                }

                try {
                    courseRepository.submitReview(courseId, rating, reviewContent.value)
                    _event.emit(UiEvent.SubmitReviewSuccess)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: NoNetworkException) {
                    _event.emit(UiEvent.NoNetwork)
                } catch (exception: HttpException) {
                    _event.emit(
                        when (exception.code()) {
                            400 -> {
                                UiEvent.CourseAlreadyReviewed
                            }

                            401 -> {
                                _authDialog.value = AuthFeature.SubmitReview(courseId)
                                return@launch
                            }

                            else -> {
                                UiEvent.UnknownFailure
                            }
                        },
                    )
                } catch (_: Throwable) {
                    _event.emit(UiEvent.UnknownFailure)
                }
            }
        }

        fun onAuthSuccess(authFeature: AuthFeature) {
            dismissAuthDialog()
            if (authFeature is AuthFeature.SubmitReview) {
                submitReview(authFeature.courseId)
            }
        }

        fun dismissAuthDialog() {
            _authDialog.value = null
        }

        sealed interface UiEvent {
            data object SubmitReviewSuccess : UiEvent

            data object NoNetwork : UiEvent

            data object CourseAlreadyReviewed : UiEvent

            data object InvalidReviewContent : UiEvent

            data object UnknownFailure : UiEvent
        }

        companion object {
            const val MAX_REVIEW_LENGTH = 1_000
        }
    }
