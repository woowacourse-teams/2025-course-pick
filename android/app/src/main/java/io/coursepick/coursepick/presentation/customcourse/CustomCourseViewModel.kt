package io.coursepick.coursepick.presentation.customcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.CoursesPage
import io.coursepick.coursepick.domain.customcourse.CustomCourseRepository
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.auth.AuthFeature
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.course.UiStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomCourseViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val customCourseRepository: CustomCourseRepository,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<CustomCourseUiEvent>()
        val uiEvent: SharedFlow<CustomCourseUiEvent> get() = _uiEvent.asSharedFlow()

        private val _authDialogState = MutableStateFlow<AuthFeature?>(null)
        val authDialogState: StateFlow<AuthFeature?> get() = _authDialogState.asStateFlow()

        private val _state =
            MutableStateFlow(
                CustomCourseUiState(
                    customCourses = emptyList(),
                    status = UiStatus.Loading,
                ),
            )

        val state: StateFlow<CustomCourseUiState> = _state.asStateFlow()

        fun checkAuthAndShowDialog() {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _authDialogState.value = AuthFeature.CustomCourse
                }
            }
        }

        fun onGoToCreateCustomCourse() {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _authDialogState.value = AuthFeature.CustomCourse
                } else {
                    _uiEvent.emit(CustomCourseUiEvent.NavigateToCreateCourse)
                }
            }
        }

        fun dismissAuthDialog() {
            _authDialogState.value = null
        }

        fun onAuthSuccess(feature: AuthFeature) {
            if (feature is AuthFeature.CustomCourse) {
                dismissAuthDialog()
                viewModelScope.launch {
                    _uiEvent.emit(CustomCourseUiEvent.NavigateToCreateCourse)
                }
            }
        }

        fun fetchCustomCourses(
            userCoordinate: Coordinate?,
            onFirstItemLoaded: (CustomCourseItem) -> Unit,
        ) {
            _state.update { currentState ->
                currentState.copy(status = UiStatus.Loading)
            }
            viewModelScope.launch {
                runCatching {
                    customCourseRepository.customCourses(userCoordinate = userCoordinate)
                }.onSuccess { coursesPage: CoursesPage ->
                    Logger.log(Logger.Event.Success("fetch_custom_courses_new"))

                    val customCourseItems: List<CustomCourseItem> =
                        coursesPage.courses.mapIndexed { index, course ->
                            CustomCourseItem(
                                course = course,
                                selected = index == 0,
                            )
                        }

                    onFirstItemLoaded(customCourseItems.first())

                    _state.update { currentState ->
                        currentState.copy(
                            status = UiStatus.Success,
                            customCourses = customCourseItems,
                            selectedCustomCourse = customCourseItems.first(),
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
                                status = UiStatus.Failure,
                                customCourses = emptyList(),
                            )
                        }
                        return@onFailure
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

        fun select(customCourse: CustomCourseItem) {
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

        fun onNavigateToCourse(
            customCourse: CustomCourseItem,
            onNavigateTo: (CourseItem) -> Unit,
        ) {
            select(customCourse)
            val courseItem: CourseItem = _state.value.selectedCustomCourse?.toCourseItem() ?: return
            onNavigateTo(courseItem)
        }
    }
