package io.coursepick.coursepick.view

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude

data class CourseItem(
    private val course: Course,
    val selected: Boolean,
) {
    val id: Long = course.id
    val name: String = course.name.value
    val distance: Int = course.distance.meter
    val length: Int = course.length.meter
    val coordinates: List<Coordinate> = course.coordinates

    private val westernmost: Latitude =
        coordinates.minBy { coordinate: Coordinate -> coordinate.latitude.value }.latitude
    private val easternmost: Latitude =
        coordinates.maxBy { coordinate: Coordinate -> coordinate.latitude.value }.latitude
    private val southernmost: Longitude =
        coordinates.minBy { coordinate: Coordinate -> coordinate.longitude.value }.longitude
    private val northernmost: Longitude =
        coordinates.maxBy { coordinate: Coordinate -> coordinate.longitude.value }.longitude

    val northwest: Coordinate = Coordinate(westernmost, northernmost)
    val northeast: Coordinate = Coordinate(easternmost, northernmost)
    val southeast: Coordinate = Coordinate(easternmost, southernmost)
    val southwest: Coordinate = Coordinate(westernmost, southernmost)
}
