package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseDifficulty
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
        val id: Long?,
        val name: String?,
        val distance: Double?,
        val length: Double?,
    )

    fun toCourseOrNull(): Course? {
        val coordinates: List<Coordinate> =
            geometry?.coordinates?.map { coordinate: List<Double?>? ->
                if (coordinate == null) return null
                Coordinate(
                    Latitude(coordinate[0] ?: return null),
                    Longitude(coordinate[1] ?: return null),
                )
            } ?: return null
        if (properties == null) return null
        return Course(
            id = properties.id ?: return null,
            name = CourseName(properties.name ?: return null),
            distance = Distance(properties.distance?.toInt() ?: return null),
            length = Length(properties.length?.toInt() ?: return null),
            coordinates = coordinates,
        )
    }

    private fun courseDifficulty(value: Double): CourseDifficulty? =
        when {
            value > 0.0 && value < 3.0 -> CourseDifficulty.EASY
            value >= 3.0 && value < 6.0 -> CourseDifficulty.NORMAL
            value >= 6.0 && value < 10.0 -> CourseDifficulty.HARD
            else -> null
        }
}
