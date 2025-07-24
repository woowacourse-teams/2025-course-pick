package io.coursepick.coursepick.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.coursepick.coursepick.data.DefaultCourseRepository
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val courseRepository: CourseRepository = DefaultCourseRepository(),
) : ViewModel() {
    private val _state: MutableLiveData<MainUiState> =
        MutableLiveData(
            MainUiState(
                courses = emptyList(),
                isLoading = true,
            ),
        )
    val state: LiveData<MainUiState> get() = _state

    private val _event: MutableSingleLiveData<MainUiEvent> = MutableSingleLiveData()
    val event: SingleLiveData<MainUiEvent> get() = _event

    fun select(selectedCourse: CourseItem) {
        if (selectedCourse.selected) {
            _event.value = MainUiEvent.SelectNewCourse(selectedCourse)
            return
        }

        val oldCourses: List<CourseItem> = state.value?.courses ?: return

        val selectedIndex = oldCourses.indexOf(selectedCourse)
        if (selectedIndex == -1) return

        val newCourses: List<CourseItem> = newCourses(oldCourses, selectedCourse)
        _state.value = state.value?.copy(courses = newCourses)
        _event.value = MainUiEvent.SelectNewCourse(selectedCourse)
    }

    fun navigate(
        selectedCourse: CourseItem,
        location: Coordinate,
    ): String = url(location, selectedCourse.coordinates[0], selectedCourse.name)

    fun fetchCourses(coordinate: Coordinate) {
        viewModelScope.launch {
            runCatching {
                courseRepository.courses(coordinate.latitude, coordinate.longitude)
            }.onSuccess { courses: List<Course> ->
                val courses: List<CourseItem> =
                    courses
                        .sortedBy { course: Course -> course.distance }
                        .mapIndexed { index: Int, course: Course ->
                            CourseItem(
                                course,
                                index == 0,
                            )
                        }
                _state.value = MainUiState(courses)
                _event.value = MainUiEvent.FetchCourseSuccess(courses.firstOrNull())
            }.onFailure { error: Throwable ->
                _event.value = MainUiEvent.FetchCourseFailure
            }
        }
    }

    private fun url(
        start: Coordinate,
        end: Coordinate,
        endName: String,
    ): String {
        val startName = "현재 위치"
        return "https://map.kakao.com/link/by/walk/" +
            "$startName,${start.latitude.value},${start.longitude.value}/$endName,${end.latitude.value},${end.longitude.value}"
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
