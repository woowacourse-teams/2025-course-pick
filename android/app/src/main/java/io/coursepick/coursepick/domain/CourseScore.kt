package io.coursepick.coursepick.domain

@JvmInline
value class CourseScore(
    val value: Double,
) {
    init {
        require(value > MIN_SCORE && value < MAX_SCORE) { "점수는 $MIN_SCORE 초과 $MAX_SCORE 미만이어야 합니다. 입력값: $value" }
    }

    companion object {
        private const val MIN_SCORE = 0.0
        private const val MAX_SCORE = 10.0
    }
}
