package io.coursepick.coursepick.presentation.customcourse

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.presentation.course.CourseItem

data class CustomCourseItem(
    val course: Course,
    val selected: Boolean,
) {
    val id: String = course.id
    val name: String = course.name.value
    val distance: Distance? = course.distance
    val length: Length = course.length
    val coordinates: List<Coordinate> = course.coordinates

    fun select(): CustomCourseItem = copy(selected = true)

    fun deselect(): CustomCourseItem = copy(selected = false)
}

fun CustomCourseItem.toCourseItem(): CourseItem =
    CourseItem(
        course = course,
        selected = selected,
    )
