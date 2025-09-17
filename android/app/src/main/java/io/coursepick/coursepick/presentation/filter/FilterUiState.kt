package io.coursepick.coursepick.presentation.filter

import io.coursepick.coursepick.domain.course.Difficulty

data class FilterUiState(
    val difficulties: Set<Difficulty> = setOf(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
    val lengthMinimum: Int = MINIMUM_LENGTH_RANGE,
    val lengthMaximum: Int = MAXIMUM_LENGTH_RANGE,
) {
    fun toCondition(): FilterCondition {
        val min = lengthMinimum * 1000
        val max = if (lengthMaximum == MAXIMUM_LENGTH_RANGE) Int.MAX_VALUE else lengthMaximum * 1000
        return FilterCondition(
            difficulties = difficulties,
            lengthRange = LengthRange(min, max),
        )
    }

    companion object {
        private const val MINIMUM_LENGTH_RANGE = 0
        private const val MAXIMUM_LENGTH_RANGE = 21

        fun fromCondition(condition: FilterCondition): FilterUiState {
            val min = condition.lengthRange.minimum / 1000
            val max =
                if (condition.lengthRange.maximum == Int.MAX_VALUE) MAXIMUM_LENGTH_RANGE else condition.lengthRange.maximum / 1000
            return FilterUiState(
                difficulties = condition.difficulties,
                lengthMinimum = min,
                lengthMaximum = max,
            )
        }
    }
}
