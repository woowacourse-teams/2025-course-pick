package io.coursepick.coursepick.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseRepository
import io.coursepick.coursepick.domain.DefaultCourseRepository

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

    init {
        fetchCourses()
    }

    fun select(selectedCourse: CourseItem) {
        if (selectedCourse.selected) return
        val oldCourses: List<CourseItem> = state.value?.courses ?: return

        val selectedIndex = oldCourses.indexOf(selectedCourse)
        if (selectedIndex == -1) return

        val newCourses: List<CourseItem> = newCourses(oldCourses, selectedCourse)
        _state.value = state.value?.copy(courses = newCourses)
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

    private fun fetchCourses() {
        _state.value =
            MainUiState(
                courseRepository.courses
                    .sortedBy { course: Course -> course.distance.meter }
                    .mapIndexed { index: Int, course: Course ->
                        CourseItem(
                            course,
                            index == 0,
                        )
                    },
            )
    }
}
