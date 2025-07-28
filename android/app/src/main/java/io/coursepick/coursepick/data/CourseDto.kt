package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseDifficulty
import io.coursepick.coursepick.domain.CourseName
import io.coursepick.coursepick.domain.Distance
import io.coursepick.coursepick.domain.Length
import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    val id: Long,
    val name: String,
    val distance: Int,
    val length: Int,
    val roadType: String,
    val difficulty: Double,
    val segmentDtos: List<SegmentDto>,
) {
    fun toCourse(): Course =
        Course(
            id = id,
            name = CourseName(name),
            distance = Distance(distance),
            length = Length(length),
            roadType = roadType,
            difficulty = CourseDifficulty(difficulty),
            segments = segmentDtos.map(SegmentDto::toSegment),
        )

    private fun CourseDifficulty(value: Double): CourseDifficulty =
        when {
            value > 0.0 && value < 3.0 -> CourseDifficulty.EASY
            value >= 3.0 && value < 6.0 -> CourseDifficulty.NORMAL
            value >= 6.0 && value < 10.0 -> CourseDifficulty.HARD
            else -> CourseDifficulty.UNKNOWN
        }
}
