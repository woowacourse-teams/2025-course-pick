package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude
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
