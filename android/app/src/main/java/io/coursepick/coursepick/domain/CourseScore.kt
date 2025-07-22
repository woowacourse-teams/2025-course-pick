package io.coursepick.coursepick.domain

@JvmInline
value class CourseScore(
    val value: Double,
) {
    init {
        require(value > LOWER_BOUND && value < UPPER_BOUND) { "점수는 $LOWER_BOUND 초과 $UPPER_BOUND 미만이어야 합니다. 입력값: $value" }
    }

    fun courseDifficulty(): CourseDifficulty =
        when {
            value < 3.0 -> CourseDifficulty.EASY
            value < 7.0 -> CourseDifficulty.NORMAL
            else -> CourseDifficulty.HARD
        }

    companion object {
        private const val LOWER_BOUND = 0.0
        private const val UPPER_BOUND = 10.0
    }
}
