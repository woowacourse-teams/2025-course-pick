package io.coursepick.coursepick.presentation.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.presentation.Logger
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
        private val courseRepository: CourseRepository,
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent: SharedFlow<UiEvent> get() = _uiEvent.asSharedFlow()

        private val rating = MutableStateFlow<Float?>(null)
        private val reviewContent = MutableStateFlow("")
        private val isSubmitting = MutableStateFlow(false)

        val uiState: StateFlow<UiState> =
            combine(rating, reviewContent, isSubmitting) { rating: Float?, reviewContent: String, isSubmitting: Boolean ->
                UiState(
                    rating = rating,
                    reviewContent = reviewContent,
                    canSubmit = rating != null && reviewContent.isNotBlank(),
                    isSubmitting = isSubmitting,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState(),
            )

        private val _dialogState = MutableStateFlow(DialogState())
        val dialogState: StateFlow<DialogState> get() = _dialogState.asStateFlow()

        fun setRating(rating: Float) {
            this.rating.value = rating
        }

        fun setReviewContent(text: String) {
            reviewContent.value = text.take(MAX_REVIEW_LENGTH)
        }

        fun submitReview(courseId: String) {
            if (isSubmitting.value) return

            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _dialogState.value = dialogState.value.copy(authDialog = AuthFeature.WriteReview(courseId))
                    return@launch
                }

                val rating: Float? = rating.value
                if (rating == null) {
                    _uiEvent.emit(UiEvent.NoRating)
                    return@launch
                }

                if (reviewContent.value.isBlank()) {
                    _uiEvent.emit(UiEvent.EmptyContent)
                    return@launch
                }

                try {
                    isSubmitting.value = true
                    courseRepository.submitReview(courseId, rating, reviewContent.value)
                    _uiEvent.emit(UiEvent.SubmitReviewSuccess)

                    Logger.log(
                        Logger.Event.Success("submit_review"),
                        "courseId" to courseId,
                        "rating" to rating,
                        "content" to reviewContent.value,
                    )

                    this@WriteCourseReviewViewModel.rating.value = null
                    reviewContent.value = ""
                } catch (exception: Throwable) {
                    Logger.log(
                        Logger.Event.Failure("submit_review"),
                        "exception" to exception.message.orEmpty(),
                        "courseId" to courseId,
                        "rating" to rating,
                        "content" to reviewContent.value,
                    )

                    when (exception) {
                        is CancellationException -> {
                            throw exception
                        }

                        is NoNetworkException -> {
                            _uiEvent.emit(UiEvent.NoNetwork)
                        }

                        is HttpException -> {
                            _uiEvent.emit(
                                when (exception.code()) {
                                    400 -> {
                                        UiEvent.CourseAlreadyReviewed
                                    }

                                    401 -> {
                                        _dialogState.value = dialogState.value.copy(authDialog = AuthFeature.WriteReview(courseId))
                                        return@launch
                                    }

                                    else -> {
                                        UiEvent.UnknownFailure
                                    }
                                },
                            )
                        }

                        else -> {
                            _uiEvent.emit(UiEvent.UnknownFailure)
                        }
                    }
                } finally {
                    isSubmitting.value = false
                }
            }
        }

        fun dismissAuthDialog() {
            _dialogState.value = dialogState.value.copy(authDialog = null)
        }

        fun onAuthSuccess(authFeature: AuthFeature) {
            dismissAuthDialog()
            if (authFeature is AuthFeature.WriteReview) {
                submitReview(authFeature.courseId)
            }
        }

        fun onExit() {
            viewModelScope.launch {
                if (reviewContent.value.isBlank()) {
                    confirmExit()
                } else {
                    _dialogState.value = dialogState.value.copy(showExitDialog = true)
                }
            }
        }

        fun confirmExit() {
            viewModelScope.launch {
                dismissExitDialog()
                _uiEvent.emit(UiEvent.Exit)
            }
        }

        fun dismissExitDialog() {
            _dialogState.value = dialogState.value.copy(showExitDialog = false)
        }

        sealed interface UiEvent {
            data object Exit : UiEvent

            data object SubmitReviewSuccess : UiEvent

            data object NoNetwork : UiEvent

            data object CourseAlreadyReviewed : UiEvent

            data object NoRating : UiEvent

            data object EmptyContent : UiEvent

            data object UnknownFailure : UiEvent
        }

        data class UiState(
            val rating: Float? = null,
            val reviewContent: String = "",
            val canSubmit: Boolean = false,
            val isSubmitting: Boolean = false,
        )

        data class DialogState(
            val authDialog: AuthFeature? = null,
            val showExitDialog: Boolean = false,
        )

        companion object {
            const val MAX_REVIEW_LENGTH = 500
        }
    }
