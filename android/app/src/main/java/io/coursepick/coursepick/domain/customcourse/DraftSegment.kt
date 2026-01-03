package io.coursepick.coursepick.domain.customcourse

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Length

data class DraftSegment(
    val coordinates: List<Coordinate>,
    val length: Length,
)
