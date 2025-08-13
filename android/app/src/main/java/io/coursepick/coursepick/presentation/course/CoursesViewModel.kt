package io.coursepick.coursepick.presentation.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.coursepick.coursepick.data.DefaultCourseRepository
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseRepository
import io.coursepick.coursepick.presentation.MutableSingleLiveData
import io.coursepick.coursepick.presentation.SingleLiveData
import kotlinx.coroutines.launch

class CoursesViewModel(
    private val courseRepository: CourseRepository = DefaultCourseRepository(),
) : ViewModel() {
    private val _state: MutableLiveData<CoursesUiState> =
        MutableLiveData(
            CoursesUiState(
                courses = emptyList(),
                isLoading = true,
            ),
        )
    val state: LiveData<CoursesUiState> get() = _state

    private val _event: MutableSingleLiveData<CoursesUiEvent> = MutableSingleLiveData()
    val event: SingleLiveData<CoursesUiEvent> get() = _event

    fun select(selectedCourse: CourseItem) {
        if (selectedCourse.selected) {
            _event.value = CoursesUiEvent.SelectNewCourse(selectedCourse)
            return
        }

        val oldCourses: List<CourseItem> = state.value?.courses ?: return

        val selectedIndex = oldCourses.indexOf(selectedCourse)
        if (selectedIndex == -1) return

        val newCourses: List<CourseItem> = newCourses(oldCourses, selectedCourse)
        _state.value = state.value?.copy(courses = newCourses)
        _event.value = CoursesUiEvent.SelectNewCourse(selectedCourse)
    }

    fun fetchCourses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate? = null,
    ) {
        viewModelScope.launch {
            runCatching {
                courseRepository.courses(mapCoordinate, userCoordinate)
            }.onSuccess { courses: List<Course> ->
                val courseItems: List<CourseItem> =
                    courses
                        .sortedBy { course: Course -> course.distance }
                        .mapIndexed { index: Int, course: Course ->
                            CourseItem(
                                course,
                                index == 0,
                            )
                        }
                _state.value = CoursesUiState(courseItems)
                _event.value = CoursesUiEvent.FetchCourseSuccess(courseItems.firstOrNull())
            }.onFailure {
                _event.value = CoursesUiEvent.FetchCourseFailure
            }
        }
    }

    fun fetchNearestCoordinate(
        selectedCourse: CourseItem,
        location: Coordinate,
    ) {
        viewModelScope.launch {
            runCatching {
                courseRepository.nearestCoordinate(selectedCourse.course, location)
            }.onSuccess { nearest: Coordinate ->
                _event.value =
                    CoursesUiEvent.FetchNearestCoordinateSuccess(
                        origin = location,
                        destination = nearest,
                        destinationName = selectedCourse.name,
                    )
            }.onFailure {
                _event.value = CoursesUiEvent.FetchNearestCoordinateFailure
            }
        }
    }

    private fun newCourses(
        oldCourses: List<CourseItem>,
        selectedCourse: CourseItem,
    ): List<CourseItem> =
        oldCourses.map { course: CourseItem ->
            if (course == selectedCourse) {
                course.copy(selected = true)
            } else {
                course.copy(selected = false)
            }
        }
}
