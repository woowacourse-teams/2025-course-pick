package io.coursepick.coursepick.presentation.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.model.Difficulty
import io.coursepick.coursepick.presentation.ui.MutableSingleLiveData
import io.coursepick.coursepick.presentation.ui.SingleLiveData

class FilterViewModel(
    initCourseFilter: CourseFilter,
    private var courses: List<CourseItem>,
) : ViewModel() {
    private val _state = MutableLiveData(FilterUiState.from(initCourseFilter))
    val state: LiveData<FilterUiState> = _state

    private val _event: MutableSingleLiveData<FilterUiEvent> = MutableSingleLiveData()
    val event: SingleLiveData<FilterUiEvent> get() = _event

    fun toggleDifficulty(difficulty: Difficulty) {
        _state.value =
            state.value?.let { state ->
                val updatedDifficulties =
                    state.difficulties.toMutableSet().apply {
                        if (contains(difficulty)) remove(difficulty) else add(difficulty)
                    }
                state.copy(difficulties = updatedDifficulties)
            }
        updateFilteredCoursesCount()
    }

    fun updateLengthRange(
        min: Int,
        max: Int,
    ) {
        val currentRange = _state.value ?: return

        _state.value =
            currentRange.copy(
                minimumLengthKm = min,
                maximumLengthKm = max,
            )

        updateFilteredCoursesCount()
    }

    fun resetFilterToDefault() {
        _state.value = FilterUiState()
        updateFilteredCoursesCount()
    }

    fun cancel() {
        _event.value = FilterUiEvent.FilterCancel
    }

    fun result() {
        _event.value = FilterUiEvent.FilterResult
    }

    fun updateCourses(courseItems: List<CourseItem>) {
        courses = courseItems
        updateFilteredCoursesCount()
    }

    private fun updateFilteredCoursesCount() {
        val courseFilter = _state.value?.toCourseFilter() ?: return
        val filtered =
            if (courseFilter.difficulties.isEmpty()) {
                emptyList()
            } else {
                courses.filter { courseItem ->
                    (courseItem.difficulty in courseFilter.difficulties) &&
                        (courseItem.length in courseFilter.lengthRange.first..courseFilter.lengthRange.last)
                }
            }
        _state.value = state.value?.copy(coursesCount = filtered.size)
    }

    companion object {
        const val MINIMUM_LENGTH_RANGE = 0f
        const val MAXIMUM_LENGTH_RANGE = 21f

        fun factory(
            courseFilter: CourseFilter,
            allCourse: List<CourseItem>,
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = FilterViewModel(courseFilter, allCourse) as T
        }
    }
}
