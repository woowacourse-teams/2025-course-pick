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

    fun toggleDifficulty(difficulty: Difficulty) {
        val current = _uiState.value ?: return
        val newSet =
            current.difficulties.toMutableSet().apply {
                if (contains(difficulty)) remove(difficulty) else add(difficulty)
            }
        _uiState.value = current.copy(difficulties = newSet)
        recalcFilteredCourses()
    }

    fun updateLengthRange(
        min: Int,
        max: Int,
    ) {
        val current = _uiState.value ?: return
        _uiState.value = current.copy(lengthMinimum = min, lengthMaximum = max)
    }

    fun reset() {
        _uiState.value = FilterUiState()
        recalcFilteredCourses()
    }

    fun recalcFilteredCourses() {
        val condition = _uiState.value?.toCondition() ?: return
        val filtered =
            courses.filter { courseItem ->
                (condition.difficulties.isEmpty() || courseItem.toDomain() in condition.difficulties) &&
                    (courseItem.length in condition.lengthRange.minimum..condition.lengthRange.maximum)
            }
        _filteredCourses.value = filtered.size
    }

    fun toCondition(): FilterCondition = _uiState.value?.toCondition() ?: FilterCondition()
}
