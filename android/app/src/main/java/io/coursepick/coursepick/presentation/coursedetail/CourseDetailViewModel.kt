package io.coursepick.coursepick.presentation.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.NetworkMonitor
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.CourseDetail
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.CourseReview
import io.coursepick.coursepick.domain.favorites.FavoriteCourseRepository
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
        private val authRepository: AuthRepository,
        private val courseRepository: CourseRepository,
        private val favoriteCourseRepository: FavoriteCourseRepository,
        private val networkMonitor: NetworkMonitor,
    ) : ViewModel() {
        private val _event = MutableSharedFlow<UiEvent>()
        val event: SharedFlow<UiEvent> get() = _event.asSharedFlow()

        private val loadTrigger = MutableSharedFlow<Unit>(1)

        private val courseId = MutableStateFlow<String?>(null)

        val state: StateFlow<UiState> =
            combine(
                loadTrigger,
                courseId,
                favoriteCourseRepository.favoriteCourseIds,
            ) { _, courseId: String?, favoriteCourseIds: Set<String> ->
                if (!networkMonitor.isConnected()) return@combine UiState.Failure.NoNetwork

                val courseDetail: CourseDetail? =
                    courseId?.let { courseId: String -> runCatching { courseRepository.detail(courseId) }.getOrNull() }
                if (courseDetail == null) return@combine UiState.Failure.Unknown

                UiState.Success(
                    CourseDetailUiModel(
                        id = courseDetail.id,
                        name = courseDetail.name.value,
                        length = courseDetail.length.meter.value,
                        isFavorite = favoriteCourseIds.contains(courseDetail.id),
                        reviewCount = courseDetail.reviewCount,
                        averageRating = courseDetail.averageRating,
                        tags = courseDetail.tags,
                        reviews = courseDetail.reviews.map { review: CourseReview -> review.toUiModel() },
                    ),
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Loading,
            )

        private val _authDialog = MutableStateFlow<AuthFeature?>(null)
        val authDialog: StateFlow<AuthFeature?> get() = _authDialog.asStateFlow()

        private val _showReportCourseDialog = MutableStateFlow(false)
        val showReportCourseDialog: StateFlow<Boolean> get() = _showReportCourseDialog.asStateFlow()

        fun load(courseId: String) {
            viewModelScope.launch {
                this@CourseDetailViewModel.courseId.value = courseId
                loadTrigger.emit(Unit)
            }
        }

        private fun CourseReview.toUiModel(): CourseReviewUiModel =
            CourseReviewUiModel(
                id = id,
                authorId = authorId,
                authorName = authorName,
                isMine = false, // TODO: API 업데이트될 시 사용자 ID 기반으로 확인하도록 변경
                rating = rating.toFloat(),
                content = content,
            )

        fun toggleFavorite() {
            viewModelScope.launch {
                val currentState = state.value
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
            _authDialog.value = null
        }

        fun onAuthSuccess(feature: AuthFeature) {
            dismissAuthDialog()
            if (feature is AuthFeature.ReportCourse) {
                onReportCourse()
            }
        }

        fun onReportCourse() {
            viewModelScope.launch {
                val currentState: UiState = state.value
                if (currentState is UiState.Success) {
                    if (authRepository.accessToken() == null) {
                        _authDialog.value = AuthFeature.ReportCourse(currentState.data.id)
                    } else {
                        _showReportCourseDialog.value = true
                    }
                }
            }
        }

        fun submitCourseReport() {
            viewModelScope.launch {
                try {
                    val currentState: UiState = state.value
                    if (currentState is UiState.Success) {
                        courseRepository.reportCourse(currentState.data.id)
                        dismissReportCourseDialog()
                        _event.emit(UiEvent.ReportCourseSuccess)
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: NoNetworkException) {
                    _event.emit(UiEvent.NoNetwork)
                } catch (exception: HttpException) {
                    _event.emit(
                        when (exception.code()) {
                            400 -> {
                                dismissReportCourseDialog()
                                UiEvent.CourseAlreadyReported
                            }

                            401 -> {
                                UiEvent.ReportCourseUnauthorizedUser
                            }

                            else -> {
                                UiEvent.ReportCourseUnknownFailure
                            }
                        },
                    )
                } catch (_: Throwable) {
                    _event.emit(UiEvent.ReportCourseUnknownFailure)
                }
            }
        }

        fun dismissReportCourseDialog() {
            _showReportCourseDialog.value = false
        }

        sealed interface UiEvent {
            object ReportCourseSuccess : UiEvent

            object CourseAlreadyReported : UiEvent

            object NoNetwork : UiEvent

            object ReportCourseUnauthorizedUser : UiEvent

            object ReportCourseUnknownFailure : UiEvent
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
    }
