package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseName
import io.coursepick.coursepick.domain.Distance
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Length
import io.coursepick.coursepick.domain.Longitude
import kotlinx.serialization.Serializable

@Serializable
data class GetCoursesResponseItem(
    val type: String?,
    val geometry: Geometry?,
    val properties: Properties?,
) {
    @Serializable
    data class Geometry(
        val type: String?,
        val coordinates: List<List<Double?>?>?,
    )

    @Serializable
    data class Properties(
        val name: String?,
        val distance: Double?,
        val length: Double?,
    )

    fun toCourse(): Course {
        val coordinates: List<Coordinate> =
            geometry?.coordinates?.map { coordinate: List<Double?>? ->
                val latitude = Latitude(coordinate?.get(0) ?: 0.0)
                val longitude = Longitude(coordinate?.get(1) ?: 0.0)
                Coordinate(latitude, longitude)
            } ?: emptyList()

        return Course(
            id = 0,
            name = CourseName(properties?.name ?: ""),
            distance = Distance(properties?.distance?.toInt() ?: 0),
            length = Length(properties?.distance?.toInt() ?: 0),
            coordinates = coordinates,
        )
    }
}
