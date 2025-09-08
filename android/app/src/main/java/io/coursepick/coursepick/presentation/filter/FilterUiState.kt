package io.coursepick.coursepick.presentation.filter

import io.coursepick.coursepick.presentation.model.Difficulty

data class FilterUiState(
    val difficulties: Set<Difficulty> = setOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
    val minimumLengthKm: Int = MINIMUM_LENGTH_RANGE,
    val maximumLengthKm: Int = MAXIMUM_LENGTH_RANGE,
    val coursesCount: Int = 0,
) {
    fun toCourseFilter(): CourseFilter {
        val minimumMeter = minimumLengthKm * 1000
        val maximumMeter =
            if (maximumLengthKm == MAXIMUM_LENGTH_RANGE) Int.MAX_VALUE else maximumLengthKm * 1000
        return CourseFilter(
            difficulties = difficulties,
            lengthRange = IntRange(minimumMeter, maximumMeter),
        )
    }

    companion object {
        private const val MINIMUM_LENGTH_RANGE = 0
        private const val MAXIMUM_LENGTH_RANGE = 21

        fun from(courseFilter: CourseFilter): FilterUiState {
            val minimumLengthKm = courseFilter.lengthRange.first / 1000
            val maximumLengthKm =
                if (courseFilter.lengthRange.last == Int.MAX_VALUE) MAXIMUM_LENGTH_RANGE else courseFilter.lengthRange.last / 1000
            return FilterUiState(
                difficulties = courseFilter.difficulties,
                minimumLengthKm = minimumLengthKm,
                maximumLengthKm = maximumLengthKm,
            )
        }
    }
}
