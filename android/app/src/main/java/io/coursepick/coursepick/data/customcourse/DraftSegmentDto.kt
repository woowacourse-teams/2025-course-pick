package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto

data class DraftSegmentDto(
    val coordinates: List<CoordinateDto>,
    val length: Double,
)
