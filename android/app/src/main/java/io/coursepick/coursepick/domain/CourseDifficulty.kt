package io.coursepick.coursepick.domain

enum class CourseDifficulty {
    EASY,
    NORMAL,
    HARD,
    ;

    companion object {
        operator fun invoke(courseScore: CourseScore): CourseDifficulty {
            val score: Double = courseScore.value

            return when {
                score < 3.0 -> EASY
                score < 7.0 -> NORMAL
                else -> HARD
            }
        }
    }
}
