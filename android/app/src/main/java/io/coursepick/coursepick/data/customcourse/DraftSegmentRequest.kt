package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto
import kotlinx.serialization.Serializable

@Serializable
data class DraftSegmentRequest(
    val origin: CoordinateDto,
    val destination: CoordinateDto,
)
