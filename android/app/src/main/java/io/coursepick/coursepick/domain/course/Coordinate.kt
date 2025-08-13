package io.coursepick.coursepick.domain.course

import java.io.Serializable

data class Coordinate(
    val latitude: Latitude,
    val longitude: Longitude,
) : Serializable
