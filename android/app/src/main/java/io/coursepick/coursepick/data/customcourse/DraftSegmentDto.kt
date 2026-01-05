package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.customcourse.DraftSegment
import kotlinx.serialization.Serializable

@Serializable
data class DraftSegmentDto(
    val coordinates: List<CoordinateDto>,
    val length: Double,
) {
    fun toDraftSegment(): DraftSegment =
        DraftSegment(
            coordinates = coordinates.map(CoordinateDto::toCoordinate),
            length = Length(length),
        )
}
