package io.coursepick.coursepick.data.course

import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.Length
import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    private val id: String,
    private val name: String,
    private val distance: Double?,
    private val length: Double,
    private val coordinates: List<CoordinateDto>,
) {
    fun toCourseOrNull(): Course? =
        runCatching {
            Course(
                id = id,
                name = CourseName(name),
                distance = distance?.let { distance: Double -> Distance(distance) },
                length = Length(length),
                coordinates = coordinates.map(CoordinateDto::toCoordinate),
            )
        }.getOrNull()
}
