package io.coursepick.coursepick.domain

enum class CourseDifficulty {
    EASY,
    NORMAL,
    HARD,
    ;

    companion object {
        fun courseDifficulty(
            type: CourseType,
            score: CourseScore,
        ): CourseDifficulty {
            val adjustedScore: Double = type.difficultyOffset + score.value

            return when {
                adjustedScore < 3.0 -> EASY
                adjustedScore < 6.0 -> NORMAL
                else -> HARD
            }
        }
    }
}
