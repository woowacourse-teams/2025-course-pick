package io.coursepick.coursepick.domain

enum class CourseDifficulty {
    EASY,
    NORMAL,
    HARD,
    ;

    companion object {
        fun courseDifficulty(score: CourseScore): CourseDifficulty {
            val adjustedScore: Double = score.value

            return when {
                adjustedScore < 3.0 -> EASY
                adjustedScore < 7.0 -> NORMAL
                else -> HARD
            }
        }
    }
}
