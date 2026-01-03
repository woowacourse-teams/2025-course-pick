package io.coursepick.coursepick.domain.customcourse

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.CourseName

data class DraftCourse(
    val name: CourseName,
    val roadType: RoadType,
    val difficulty: Difficulty,
    val coordinates: List<Coordinate>,
)
