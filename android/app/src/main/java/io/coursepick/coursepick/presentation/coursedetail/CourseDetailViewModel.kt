package io.coursepick.coursepick.presentation.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.NetworkMonitor
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.CourseDetail
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.favorites.FavoriteCourseRepository
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
class CourseDetailViewModel
    @Inject
    constructor(
        private val courseRepository: CourseRepository,
        private val favoriteCourseRepository: FavoriteCourseRepository,
        private val authRepository: AuthRepository,
        private val networkMonitor: NetworkMonitor,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent: SharedFlow<UiEvent> get() = _uiEvent.asSharedFlow()

        private val isConnected = MutableStateFlow(networkMonitor.isConnected())
        private val isLoading = MutableStateFlow(true)
        private val courseDetail = MutableStateFlow<CourseDetail?>(null)

        val uiState: StateFlow<UiState> =
            combine(
                isConnected,
                isLoading,
                courseDetail,
                favoriteCourseRepository.favoriteCourseIds,
            ) { isConnected: Boolean, isLoading: Boolean, courseDetail: CourseDetail?, favoriteCourseIds: Set<String> ->
                if (!isConnected) {
                    UiState.Failure.NoNetwork
                } else if (isLoading) {
                    UiState.Loading
                } else if (courseDetail == null) {
                    UiState.Failure.Unknown
                } else {
                    UiState.Success(
                        courseDetail.toUiModel(
                            isFavorite = favoriteCourseIds.contains(courseDetail.id),
                            userId = authRepository.userId(),
                        ),
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Loading,
            )

        private val _dialogState = MutableStateFlow(DialogState())
        val dialogState: StateFlow<DialogState> get() = _dialogState.asStateFlow()

        fun load(courseId: String) {
            if (!networkMonitor.isConnected()) {
                isConnected.value = false
                return
            }

            viewModelScope.launch {
                isConnected.value = true
                isLoading.value = true
                courseDetail.value = courseDetail(courseId)
                isLoading.value = false
            }
        }

        private suspend fun courseDetail(courseId: String): CourseDetail? =
            runCatching {
                courseRepository.detail(courseId)
            }.onSuccess { detail: CourseDetail ->
                Logger.log(
                    Logger.Event.Success("fetch_course_detail"),
                    "courseId" to detail.id,
                    "courseName" to detail.name.value,
                )
            }.onFailure { exception: Throwable ->
                Logger.log(
                    Logger.Event.Failure("fetch_course_detail"),
                    "exception" to exception.message.orEmpty(),
                )

                if (exception is CancellationException) throw exception
            }.getOrNull()

        fun toggleFavorite() {
            viewModelScope.launch {
                val currentState = uiState.value
                if (currentState is UiState.Success) {
                    if (currentState.data.isFavorite) {
                        favoriteCourseRepository.removeFavorite(currentState.data.id)
                    } else {
                        favoriteCourseRepository.addFavorite(currentState.data.id)
                    }
                }
            }
        }

        fun dismissAuthDialog() {
            _dialogState.value = dialogState.value.copy(authDialog = null)
        }

        fun onAuthSuccess(feature: AuthFeature) {
            dismissAuthDialog()

            when (feature) {
                is AuthFeature.ReportCourse -> onReportCourse()
                is AuthFeature.DeleteReview -> onDeleteReview(feature.review)
                is AuthFeature.ReportReview -> onReportReview(feature.review)
                is AuthFeature.WriteReview -> onWriteReview()
                else -> Unit
            }
        }

        fun onReportCourse() {
            viewModelScope.launch {
                (uiState.value as? UiState.Success)?.let { uiState: UiState.Success ->
                    _dialogState.value =
                        if (authRepository.accessToken() == null) {
                            dialogState.value.copy(authDialog = AuthFeature.ReportCourse(uiState.data.id))
                        } else {
                            dialogState.value.copy(reportCourseDialog = uiState.data.name)
                        }
                }
            }
        }

        fun dismissReportCourseDialog() {
            _dialogState.value = dialogState.value.copy(reportCourseDialog = null)
        }

        fun submitCourseReport() {
            viewModelScope.launch {
                (uiState.value as? UiState.Success)?.let { uiState: UiState.Success ->
                    try {
                        courseRepository.reportCourse(uiState.data.id)
                        dismissReportCourseDialog()
                        _uiEvent.emit(UiEvent.ReportCourseSuccess)

                        Logger.log(
                            Logger.Event.Success("submit_course_report"),
                            "courseId" to uiState.data.id,
                            "courseName" to uiState.data.name,
                        )
                    } catch (exception: Throwable) {
                        Logger.log(
                            Logger.Event.Failure("submit_course_report"),
                            "exception" to exception.message.orEmpty(),
                            "courseId" to uiState.data.id,
                            "courseName" to uiState.data.name,
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
                                            dismissReportCourseDialog()
                                            UiEvent.CourseAlreadyReported
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
                    }
                }
            }
        }

        fun onDeleteReview(review: CourseReviewUiModel) {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _dialogState.value = dialogState.value.copy(authDialog = AuthFeature.DeleteReview(review))
                } else {
                    _dialogState.value = dialogState.value.copy(deleteReviewDialog = review)
                }
            }
        }

        fun dismissDeleteReviewDialog() {
            _dialogState.value = dialogState.value.copy(deleteReviewDialog = null)
        }

        fun confirmDeleteReview(review: CourseReviewUiModel) {
            dismissDeleteReviewDialog()

            viewModelScope.launch {
                (uiState.value as? UiState.Success)?.let { uiState: UiState.Success ->
                    try {
                        courseRepository.deleteReview(uiState.data.id, review.id)
                        _uiEvent.emit(UiEvent.DeleteReviewSuccess)
                        courseDetail.value = courseDetail(uiState.data.id)

                        Logger.log(
                            Logger.Event.Success("delete_review"),
                            "courseId" to uiState.data.id,
                            "reviewId" to review.id,
                            "reviewContent" to review.content,
                        )
                    } catch (exception: Throwable) {
                        Logger.log(
                            Logger.Event.Success("delete_review"),
                            "exception" to exception.message.orEmpty(),
                            "courseId" to uiState.data.id,
                            "reviewId" to review.id,
                            "reviewContent" to review.content,
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
                                        401 -> UiEvent.UnauthorizedUser
                                        else -> UiEvent.UnknownFailure
                                    },
                                )
                            }

                            else -> {
                                _uiEvent.emit(UiEvent.UnknownFailure)
                            }
                        }
                    }
                }
            }
        }

        fun onReportReview(review: CourseReviewUiModel) {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _dialogState.value = dialogState.value.copy(authDialog = AuthFeature.ReportReview(review))
                } else {
                    _dialogState.value = dialogState.value.copy(reportReviewDialog = review)
                }
            }
        }

        fun confirmReportReview(review: CourseReviewUiModel) {
            dismissReportReviewDialog()

            viewModelScope.launch {
                (uiState.value as? UiState.Success)?.let { uiState: UiState.Success ->
                    try {
                        courseRepository.reportReview(uiState.data.id, review.id)
                        _uiEvent.emit(UiEvent.ReportReviewSuccess)

                        Logger.log(
                            Logger.Event.Success("report_review"),
                            "courseId" to uiState.data.id,
                            "reviewId" to review.id,
                            "reviewContent" to review.content,
                        )
                    } catch (exception: Throwable) {
                        Logger.log(
                            Logger.Event.Success("report_review"),
                            "exception" to exception.message.orEmpty(),
                            "courseId" to uiState.data.id,
                            "reviewId" to review.id,
                            "reviewContent" to review.content,
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
                                        400 -> UiEvent.ReviewAlreadyReported
                                        401 -> UiEvent.UnauthorizedUser
                                        else -> UiEvent.UnknownFailure
                                    },
                                )
                            }

                            else -> {
                                _uiEvent.emit(UiEvent.UnknownFailure)
                            }
                        }
                    }
                }
            }
        }

        fun dismissReportReviewDialog() {
            _dialogState.value = dialogState.value.copy(reportReviewDialog = null)
        }

        fun onWriteReview() {
            viewModelScope.launch {
                (uiState.value as? UiState.Success)?.let { uiState: UiState.Success ->
                    if (authRepository.accessToken() == null) {
                        _dialogState.value = dialogState.value.copy(authDialog = AuthFeature.WriteReview(uiState.data.id))
                        return@launch
                    }

                    val alreadyReviewed: Boolean =
                        uiState.data.reviews.any { review: CourseReviewUiModel -> review.authorId == authRepository.userId() }
                    _uiEvent.emit(
                        if (alreadyReviewed) {
                            UiEvent.CourseAlreadyReviewed
                        } else {
                            UiEvent.NavigateToWriteCourseReview(uiState.data)
                        },
                    )
                }
            }
        }

        sealed interface UiEvent {
            data object NoNetwork : UiEvent

            data object UnauthorizedUser : UiEvent

            data object UnknownFailure : UiEvent

            data object ReportCourseSuccess : UiEvent

            data object CourseAlreadyReported : UiEvent

            data object DeleteReviewSuccess : UiEvent

            data object ReportReviewSuccess : UiEvent

            data object ReviewAlreadyReported : UiEvent

            data class NavigateToWriteCourseReview(
                val courseDetail: CourseDetailUiModel,
            ) : UiEvent

            data object CourseAlreadyReviewed : UiEvent
        }

        sealed interface UiState {
            data object Loading : UiState

            data class Success(
                val data: CourseDetailUiModel,
            ) : UiState

            sealed interface Failure : UiState {
                data object NoNetwork : Failure

                data object Unknown : Failure
            }
        }

        data class DialogState(
            val authDialog: AuthFeature? = null,
            val reportCourseDialog: String? = null,
            val deleteReviewDialog: CourseReviewUiModel? = null,
            val reportReviewDialog: CourseReviewUiModel? = null,
        )
    }
