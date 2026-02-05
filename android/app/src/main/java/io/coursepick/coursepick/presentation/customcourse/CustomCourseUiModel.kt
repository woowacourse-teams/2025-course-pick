package io.coursepick.coursepick.presentation.customcourse

import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.presentation.model.formatted

data class CustomCourseUiModel(
    val course: Course,
    val selected: Boolean,
) {
    val distance: String? = course.distance?.let { distance: Distance -> distance.meter.formatted }
    val length: String = course.length.meter.formatted
}
