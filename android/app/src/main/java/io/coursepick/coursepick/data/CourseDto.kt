package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseDifficulty
import io.coursepick.coursepick.domain.CourseName
import io.coursepick.coursepick.domain.Distance
import io.coursepick.coursepick.domain.Length
import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    private val id: Long,
    private val name: String,
    private val distance: Double,
    private val length: Double,
    private val roadType: String,
    private val difficulty: Double,
    private val segments: List<SegmentDto>,
) {
    fun toCourse(): Course =
        Course(
            id = id,
            name = CourseName(name),
            distance = Distance(distance),
            length = Length(length),
            roadType = roadType,
            difficulty = CourseDifficulty(difficulty),
            segments = segments.map(SegmentDto::toSegment),
        )

    private fun CourseDifficulty(value: Double): CourseDifficulty =
        when {
            value > 0.0 && value < 3.0 -> CourseDifficulty.EASY
            value >= 3.0 && value < 6.0 -> CourseDifficulty.NORMAL
            value >= 6.0 && value < 10.0 -> CourseDifficulty.HARD
            else -> CourseDifficulty.UNKNOWN
        }
}
