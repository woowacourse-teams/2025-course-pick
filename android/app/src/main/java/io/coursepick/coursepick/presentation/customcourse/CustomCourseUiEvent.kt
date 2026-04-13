package io.coursepick.coursepick.presentation.customcourse

import io.coursepick.coursepick.domain.customcourse.DraftSegment

sealed interface CustomCourseUiEvent {
    data class NewSegment(
        val segment: DraftSegment,
    ) : CustomCourseUiEvent

    data object RemoveLastWaypoint : CustomCourseUiEvent

    data object Exit : CustomCourseUiEvent
}
