package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course

data class CourseItem(
    val course: Course,
    val selected: Boolean,
    val favorite: Boolean,
) {
    val id: String = course.id
    val name: String = course.name.value
    val distance: Int? =
        course.distance
            ?.meter
            ?.value
            ?.toInt()
    val length: Int =
        course.length.meter.value
            .toInt()
    val coordinate: List<Coordinate> = course.coordinates
}
