package io.coursepick.coursepick.presentation.createcustomcourse

import io.coursepick.coursepick.domain.customcourse.DraftSegment

sealed interface CreateCustomCourseUiEvent {
    data class NewSegment(
        val segment: DraftSegment,
    ) : CreateCustomCourseUiEvent

    data object RemoveLastWaypoint : CreateCustomCourseUiEvent

    data object CourseLengthTooShort : CreateCustomCourseUiEvent

    data object Exit : CreateCustomCourseUiEvent
}
