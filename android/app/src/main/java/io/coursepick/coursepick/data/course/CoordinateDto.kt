package io.coursepick.coursepick.data.course

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude
import kotlinx.serialization.Serializable

@Serializable
data class CoordinateDto(
    private val latitude: Double,
    private val longitude: Double,
) {
    fun toCoordinate(): Coordinate =
        Coordinate(
            latitude = Latitude(latitude),
            longitude = Longitude(longitude),
        )
}
