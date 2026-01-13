package io.coursepick.coursepick.data.course

import kotlinx.serialization.Serializable

@Serializable
data class SegmentDto(
    private val inclineType: String,
    val coordinates: List<CoordinateDto>,
)
