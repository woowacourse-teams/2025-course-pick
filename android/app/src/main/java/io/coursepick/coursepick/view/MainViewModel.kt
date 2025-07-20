package io.coursepick.coursepick.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.coursepick.coursepick.data.DefaultCourseRepository
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseRepository
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude
import kotlinx.coroutines.launch
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

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
        _event.value = MainUiEvent.SelectNewCourse(selectedCourse)
    }

    private fun fetchCourses() {
        viewModelScope.launch {
            runCatching {
                courseRepository.courses(
                    Latitude(37.5165004),
                    Longitude(127.1040109),
                )
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
                _event.value = MainUiEvent.FetchCourseSuccess(courses.first())
            }.onFailure { error: Throwable ->
                _event.value = MainUiEvent.FetchCourseFailure
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
