package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseName
import io.coursepick.coursepick.domain.Distance
import io.coursepick.coursepick.domain.Length
import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    private val id: Long,
    private val name: String,
    private val distance: Double?,
    private val length: Double,
    private val roadType: String,
    private val difficulty: String,
    private val segments: List<SegmentDto>,
) {
    fun toCourse(): Course =
        Course(
            id = id,
            name = CourseName(name),
            distance = distance?.let(::Distance),
            length = Length(length),
            roadType = roadType,
            difficulty = difficulty,
            segments = segments.map(SegmentDto::toSegment),
        )
}
