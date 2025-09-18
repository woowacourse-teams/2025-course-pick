package io.coursepick.coursepick.presentation.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.coursepick.coursepick.domain.course.Difficulty
import io.coursepick.coursepick.presentation.course.CourseItem

class FilterViewModel(
    initialCondition: FilterCondition,
    val courses: List<CourseItem>,
) : ViewModel() {
    private val _uiState = MutableLiveData(FilterUiState.fromCondition(initialCondition))
    val uiState: LiveData<FilterUiState> = _uiState

    private val _filteredCourses: MutableLiveData<Int> = MutableLiveData(courses.size)
    val filteredCourses: LiveData<Int> get() = _filteredCourses

    val lengthRange = MutableLiveData(listOf(MINIMUM_LENGTH_RANGE, MAXIMUM_LENGTH_RANGE))

    fun toggleDifficulty(difficulty: Difficulty) {
        val current = _uiState.value ?: return
        val newSet =
            current.difficulties.toMutableSet().apply {
                if (contains(difficulty)) remove(difficulty) else add(difficulty)
            }
        _uiState.value = current.copy(difficulties = newSet)
        updateFilteredCoursesCount()
    }

    fun updateLengthRange(
        min: Int,
        max: Int,
    ) {
        val current = _uiState.value ?: return

        _uiState.value =
            current.copy(
                lengthMinimum = min,
                lengthMaximum = max,
            )

        lengthRange.value = listOf(min.toFloat(), max.toFloat())

        updateFilteredCoursesCount()
    }

    fun resetFilterToDefault() {
        _uiState.value = FilterUiState()
        updateFilteredCoursesCount()
    }

    private fun updateFilteredCoursesCount() {
        val condition = _uiState.value?.toCondition() ?: return
        val filtered =
            courses.filter { courseItem ->
                (condition.difficulties.isEmpty() || courseItem.toDomain() in condition.difficulties) &&
                    (courseItem.length in condition.lengthRange.minimum..condition.lengthRange.maximum)
            }
        _filteredCourses.value = filtered.size
    }

    fun toCondition(): FilterCondition = _uiState.value?.toCondition() ?: FilterCondition()

    companion object {
        const val MINIMUM_LENGTH_RANGE = 0f
        const val MAXIMUM_LENGTH_RANGE = 21f
    }
}
