package io.coursepick.coursepick.domain

enum class CourseType(
    val difficultyOffset: Int,
) {
    TRACK(-1),
    ROAD(0),
    TRAIL(1),
}
