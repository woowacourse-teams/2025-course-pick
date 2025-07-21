package io.coursepick.coursepick.domain

@JvmInline
value class CourseScore(
    val value: Double,
) {
    init {
        require(value > MIN_SCORE && value < MAX_SCORE) { }
    }

    companion object {
        private const val MIN_SCORE = 0.0
        private const val MAX_SCORE = 10.0
    }
}
