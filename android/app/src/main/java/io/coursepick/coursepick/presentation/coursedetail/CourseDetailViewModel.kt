package io.coursepick.coursepick.presentation.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.NetworkMonitor
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.CourseRepository
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
        private val courseRepository: CourseRepository,
        private val favoriteCourseRepository: FavoriteCourseRepository,
        private val authRepository: AuthRepository,
        private val networkMonitor: NetworkMonitor,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent: SharedFlow<UiEvent> get() = _uiEvent.asSharedFlow()

        private val isConnected = MutableStateFlow(networkMonitor.isConnected())
        private val isLoading = MutableStateFlow(true)
        private val courseDetail = MutableStateFlow<CourseDetailUiModel?>(null)

        val uiState: StateFlow<UiState> =
            combine(
                isConnected,
                isLoading,
                courseDetail,
                favoriteCourseRepository.favoriteCourseIds,
            ) { isConnected: Boolean, isLoading: Boolean, courseDetail: CourseDetailUiModel?, favoriteCourseIds: Set<String> ->
                if (!isConnected) {
                    UiState.Failure.NoNetwork
                } else if (isLoading) {
                    UiState.Loading
                } else if (courseDetail == null) {
                    UiState.Failure.Unknown
                } else {
                    UiState.Success(detail = courseDetail, isFavorite = favoriteCourseIds.contains(courseDetail.id))
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
                courseDetail.value = runCatching { courseRepository.detail(courseId).toUiModel() }.getOrNull()
                isLoading.value = false
            }
        }

        fun toggleFavorite() {
            viewModelScope.launch {
                val currentState = uiState.value
                if (currentState is UiState.Success) {
                    if (currentState.isFavorite) {
                        favoriteCourseRepository.removeFavorite(currentState.detail.id)
                    } else {
                        favoriteCourseRepository.addFavorite(currentState.detail.id)
                    }
                }
            }
        }

        fun dismissAuthDialog() {
            _dialogState.value = dialogState.value.copy(authDialog = null)
        }

        fun onAuthSuccess(feature: AuthFeature) {
            dismissAuthDialog()

            if (feature is AuthFeature.ReportCourse) {
                onReportCourse()
            }
        }

        fun onReportCourse() {
            viewModelScope.launch {
                (uiState.value as? UiState.Success)?.let { uiState: UiState.Success ->
                    _dialogState.value =
                        if (authRepository.accessToken() == null) {
                            dialogState.value.copy(authDialog = AuthFeature.ReportCourse(uiState.detail.id))
                        } else {
                            dialogState.value.copy(reportCourseDialog = uiState.detail.name)
                        }
                }
            }
        }

        fun dismissReportCourseDialog() {
            _dialogState.value = dialogState.value.copy(reportCourseDialog = null)
        }

        fun submitCourseReport() {
            viewModelScope.launch {
                try {
                    val currentState: UiState = uiState.value
                    if (currentState is UiState.Success) {
                        courseRepository.reportCourse(currentState.detail.id)
                        dismissReportCourseDialog()
                        _uiEvent.emit(UiEvent.ReportCourseSuccess)
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: NoNetworkException) {
                    _uiEvent.emit(UiEvent.NoNetwork)
                } catch (exception: HttpException) {
                    _uiEvent.emit(
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
                    _uiEvent.emit(UiEvent.ReportCourseUnknownFailure)
                }
            }
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
                val detail: CourseDetailUiModel,
                val isFavorite: Boolean,
            ) : UiState

            sealed interface Failure : UiState {
                data object NoNetwork : Failure

                data object Unknown : Failure
            }
        }

        data class DialogState(
            val authDialog: AuthFeature? = null,
            val reportCourseDialog: String? = null,
        )
    }
