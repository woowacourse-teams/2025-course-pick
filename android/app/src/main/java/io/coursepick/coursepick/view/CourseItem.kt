package io.coursepick.coursepick.view

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course

data class CourseItem(
    private val course: Course,
    val selected: Boolean,
) {
    val id: Long = course.id
    val name: String = course.name.value
    val distance: Int = course.distance.meter
    val length: Int = course.length.meter
    val coordinates: List<Coordinate> = course.coordinates
}
