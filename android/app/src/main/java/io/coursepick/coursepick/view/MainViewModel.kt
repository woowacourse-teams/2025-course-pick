package io.coursepick.coursepick.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseName
import io.coursepick.coursepick.domain.Distance
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Length
import io.coursepick.coursepick.domain.Longitude
import io.coursepick.coursepick.view.CourseItem.Companion.toCourseItem

class MainViewModel : ViewModel() {
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

    fun select(course: CourseItem) {
        val oldCourses: List<CourseItem> = state.value?.courses ?: return
        val selectedIndex = oldCourses.indexOf(course)
        if (selectedIndex == -1) return
        val newCourses = oldCourses.toMutableList()
        newCourses[selectedIndex] = course.copy(selected = !course.selected)
        _state.value = state.value?.copy(courses = newCourses)
    }

    private fun fetchCourses() {
        _state.value =
            MainUiState(
                List(20) {
                    Course(
                        it.toLong(),
                        CourseName("코스 $it"),
                        Distance(it * 10),
                        Length(it * 100),
                        listOf(Coordinate(Latitude(it.toDouble()), Longitude(it.toDouble()))),
                    ).toCourseItem()
                },
            )
    }
}
