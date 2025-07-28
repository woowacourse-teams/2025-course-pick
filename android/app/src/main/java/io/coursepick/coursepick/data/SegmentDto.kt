package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.InclineType
import io.coursepick.coursepick.domain.Segment
import kotlinx.serialization.Serializable

@Serializable
data class SegmentDto(
    val inclineType: String,
    val coordinateDtos: List<CoordinateDto>,
) {
    fun toSegment(): Segment =
        Segment(
            InclineType(inclineType),
            coordinateDtos.map(CoordinateDto::toCoordinate),
        )

    private fun InclineType(value: String): InclineType =
        runCatching {
            InclineType.valueOf(value)
        }.getOrElse { InclineType.UNKNOWN }
}
