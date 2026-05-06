package io.coursepick.coursepick.presentation.customcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Longitude
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomCourseViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<CustomCourseUiEvent>()
        val uiEvent: SharedFlow<CustomCourseUiEvent> get() = _uiEvent.asSharedFlow()

        private val _showAuthDialog = MutableStateFlow(false)
        val showAuthDialog: StateFlow<Boolean> get() = _showAuthDialog.asStateFlow()

        val customCourse: List<Course> =
            List(10) { index ->
                Course(
                    id = index.toString(),
                    name = CourseName("건대입구-잠실대교-종합운동장 ${index + 1}"),
                    distance = Distance(10),
                    length = Length(100),
                    coordinates =
                        listOf(
                            Coordinate(Latitude(1.0), Longitude(1.0)),
                            Coordinate(
                                Latitude(1.0 + 0.0001),
                                Longitude(1.0 + 0.0001),
                            ),
                        ),
                )
            }

        fun onGoToCreateCustomCourse() {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _showAuthDialog.value = true
                } else {
                    _uiEvent.emit(CustomCourseUiEvent.NavigateToCreateCourse)
                }
            }
        }

        fun dismissAuthDialog() {
            _showAuthDialog.value = false
        }
    }
