package io.coursepick.coursepick.presentation.customcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.NetworkMonitor
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.Outcome
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.auth.AuthenticationError
import io.coursepick.coursepick.domain.auth.Authenticator
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.CoursesPage
import io.coursepick.coursepick.domain.customcourse.CustomCourseRepository
import io.coursepick.coursepick.domain.location.LocationRepository
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.course.CourseUiModel
import io.coursepick.coursepick.presentation.course.UiStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class CustomCourseViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val customCourseRepository: CustomCourseRepository,
        private val locationRepository: LocationRepository,
        private val networkMonitor: NetworkMonitor,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<CustomCourseUiEvent>()
        val uiEvent: SharedFlow<CustomCourseUiEvent> get() = _uiEvent.asSharedFlow()

        private val _dialogState = MutableStateFlow<DialogState>(DialogState())
        val dialogState: StateFlow<DialogState> get() = _dialogState.asStateFlow()

        private val _state =
            MutableStateFlow(
                CustomCourseUiState(
                    customCourses = emptyList(),
                    status = UiStatus.Loading,
                ),
            )

        val state: StateFlow<CustomCourseUiState> get() = _state.asStateFlow()

        fun onGoToCreateCustomCourse() {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _dialogState.value = dialogState.value.copy(authFeature = AuthFeature.CreateCustomCourse)
                } else {
                    _uiEvent.emit(CustomCourseUiEvent.NavigateToCreateCourse)
                }
            }
        }

        fun dismissAuthDialog() {
            _dialogState.value = dialogState.value.copy(authFeature = null)
        }

        fun signIn(
            authenticator: Authenticator,
            authFeature: AuthFeature,
        ) {
            viewModelScope.launch {
                when (val outcome: Outcome<Unit, AuthenticationError> = authRepository.signIn(authenticator)) {
                    is Outcome.Success -> {
                        dismissAuthDialog()
                        _uiEvent.emit(CustomCourseUiEvent.AuthenticationSuccess)
                        when (authFeature) {
                            AuthFeature.FetchCustomCourses -> fetchCustomCourses()
                            is AuthFeature.DeleteCustomCourse -> onDeleteCustomCourse(authFeature.course)
                            AuthFeature.CreateCustomCourse -> _uiEvent.emit(CustomCourseUiEvent.NavigateToCreateCourse)
                        }
                    }

                    is Outcome.Failure -> {
                        _uiEvent.emit(
                            when (outcome.type) {
                                AuthenticationError.Cancelled -> {
                                    dismissAuthDialog()
                                    CustomCourseUiEvent.AuthenticationCancelled
                                }

                                AuthenticationError.Unknown -> {
                                    CustomCourseUiEvent.AuthenticationFailure
                                }
                            },
                        )
                    }
                }
            }
        }

        fun fetchCustomCourses() {
            viewModelScope.launch {
                if (!networkMonitor.isConnected()) {
                    _state.update { currentState ->
                        currentState.copy(
                            status = UiStatus.NoInternet,
                            customCourses = emptyList(),
                        )
                    }
                    return@launch
                }

                if (authRepository.accessToken() == null) {
                    _dialogState.value = dialogState.value.copy(authFeature = AuthFeature.FetchCustomCourses)
                    _state.update { currentState ->
                        currentState.copy(status = UiStatus.Success, customCourses = emptyList())
                    }
                    return@launch
                }

                _state.update { currentState ->
                    currentState.copy(status = UiStatus.Loading)
                }

                runCatching {
                    val userCoordinate: Coordinate? = locationRepository.currentLocation()?.coordinate
                    customCourseRepository.customCourses(userCoordinate)
                }.onSuccess { coursesPage: CoursesPage ->
                    Logger.log(Logger.Event.Success("fetch_custom_courses_new"))

                    val customCourse: List<CustomCourseUiModel> =
                        coursesPage.courses.mapIndexed { index, course ->
                            CustomCourseUiModel(
                                course = course,
                                selected = index == 0,
                            )
                        }

                    _state.update { currentState ->
                        currentState.copy(
                            status = UiStatus.Success,
                            customCourses = customCourse,
                            selectedCustomCourse = customCourse.firstOrNull(),
                        )
                    }
                }.onFailure { exception: Throwable ->
                    Logger.log(
                        Logger.Event.Failure("fetch_custom_courses_new"),
                        "message" to exception.message.toString(),
                    )

                    if (exception is NoNetworkException) {
                        _state.update { currentState ->
                            currentState.copy(
                                status = UiStatus.NoInternet,
                                customCourses = emptyList(),
                            )
                        }
                        return@onFailure
                    }

                    if (exception is HttpException) {
                        when (exception.code()) {
                            401 -> {
                                _uiEvent.emit(CustomCourseUiEvent.UnauthorizedUser)
                                return@onFailure
                            }

                            else -> {
                                _state.update { currentState ->
                                    currentState.copy(
                                        status = UiStatus.Failure,
                                        customCourses = emptyList(),
                                    )
                                }
                                return@onFailure
                            }
                        }
                    }

                    _state.update { currentState ->
                        currentState.copy(
                            status = UiStatus.Failure,
                            customCourses = emptyList(),
                        )
                    }
                    _uiEvent.emit(CustomCourseUiEvent.FetchCustomCourseFailure)
                }
            }
        }

        fun select(customCourse: CustomCourseUiModel) {
            val isAlreadySelected = _state.value.selectedCustomCourse?.id == customCourse.id

            if (isAlreadySelected) {
                viewModelScope.launch {
                    _uiEvent.emit(CustomCourseUiEvent.SelectCustomCourse(customCourse))
                }
                return
            }

            _state.update { currentState ->
                currentState.copy(
                    customCourses =
                        currentState.customCourses.map { item ->
                            val shouldBeSelected = (item.id == customCourse.id)
                            if (shouldBeSelected) item.select() else item.deselect()
                        },
                    selectedCustomCourse = customCourse.select(),
                )
            }
        }

        fun onDeleteCustomCourse(customCourse: CustomCourseUiModel) {
            viewModelScope.launch {
                _dialogState.value =
                    if (authRepository.accessToken() == null) {
                        dialogState.value.copy(authFeature = AuthFeature.DeleteCustomCourse(customCourse))
                    } else {
                        dialogState.value.copy(deleteCourseDialog = customCourse)
                    }
            }
        }

        fun dismissDeleteCourseDialog() {
            _dialogState.value = dialogState.value.copy(deleteCourseDialog = null)
        }

        fun confirmDeleteCustomCourse(courseId: String) {
            viewModelScope.launch {
                try {
                    customCourseRepository.deleteCourse(courseId)
                    dismissDeleteCourseDialog()
                    _uiEvent.emit(CustomCourseUiEvent.DeleteCourseSuccess)
                    fetchCustomCourses()

                    Logger.log(Logger.Event.Success("delete_custom_course"), "courseId" to courseId)
                } catch (exception: Throwable) {
                    Logger.log(
                        Logger.Event.Failure("submit_course_report"),
                        "exception" to exception.message.orEmpty(),
                        "courseId" to courseId,
                    )

                    when (exception) {
                        is CancellationException -> {
                            throw exception
                        }

                        is NoNetworkException -> {
                            _uiEvent.emit(CustomCourseUiEvent.NoNetwork)
                        }

                        is HttpException -> {
                            _uiEvent.emit(
                                when (exception.code()) {
                                    401 -> CustomCourseUiEvent.UnauthorizedUser
                                    else -> CustomCourseUiEvent.UnknownFailure
                                },
                            )
                        }

                        else -> {
                            _uiEvent.emit(CustomCourseUiEvent.UnknownFailure)
                        }
                    }
                }
            }
        }

        fun onNavigateToCourse(
            customCourse: CustomCourseUiModel,
            onNavigateTo: (CourseUiModel) -> Unit,
        ) {
            select(customCourse)
            val course: CourseUiModel = _state.value.selectedCustomCourse?.toCourseUiModel() ?: return
            onNavigateTo(course)
        }

        data class DialogState(
            val authFeature: AuthFeature? = null,
            val deleteCourseDialog: CustomCourseUiModel? = null,
        )

        sealed interface AuthFeature {
            data object FetchCustomCourses : AuthFeature

            data class DeleteCustomCourse(
                val course: CustomCourseUiModel,
            ) : AuthFeature

            data object CreateCustomCourse : AuthFeature
        }
    }
