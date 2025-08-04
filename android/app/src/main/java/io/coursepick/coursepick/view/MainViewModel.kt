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

    fun navigationUrl(
        selectedCourse: CourseItem,
        location: Coordinate,
    ): String {
        val end: Coordinate =
            selectedCourse.segments
                .first()
                .coordinates
                .first()
        val startName = "현재 위치"
        return "https://map.kakao.com/link/by/walk/" +
            "$startName,${location.latitude.value},${location.longitude.value}/${selectedCourse.name},${end.latitude.value},${end.longitude.value}"
    }

    fun fetchCourses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate? = null,
    ) {
        _state.value = _state.value?.copy(isNewPosition = false)
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
                _state.value = MainUiState(courseItems, isNewPosition = courseItems.isEmpty())
                _event.value = MainUiEvent.FetchCourseSuccess(courseItems.firstOrNull())
            }.onFailure {
                _state.value = _state.value?.copy(isNewPosition = true)
                _event.value = MainUiEvent.FetchCourseFailure
            }
        }
    }

    fun onPositionChanged() {
        _state.value = _state.value?.copy(isNewPosition = true)
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
