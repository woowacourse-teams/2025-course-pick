package io.coursepick.coursepick.view

import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.Segment

data class CourseItem(
    private val course: Course,
    val selected: Boolean,
) {
    val id: Long = course.id
    val name: String = course.name.value
    val distance: Int? = course.distance?.meter
    val length: Int = course.length.meter
    val segments: List<Segment> = course.segments
    val roadType: String = course.roadType
    val difficulty: String = course.difficulty
}
