package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto

data class DraftSegmentRequest(
    val origin: CoordinateDto,
    val destination: CoordinateDto,
)
