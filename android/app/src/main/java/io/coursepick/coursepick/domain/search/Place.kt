package io.coursepick.coursepick.domain.search

import io.coursepick.coursepick.domain.course.Coordinate

data class Place(
    val id: Long,
    val addressName: String,
    val placeName: String,
    val coordinate: Coordinate,
) {
    val latitude: Double get() = coordinate.latitude.value
    val longitude: Double get() = coordinate.longitude.value
}
