package io.coursepick.coursepick.presentation.createcustomcourse

import io.coursepick.coursepick.domain.customcourse.DraftSegment

sealed interface CreateCustomCourseUiEvent {
    data class NewSegment(
        val segment: DraftSegment,
    ) : CreateCustomCourseUiEvent

    data object RemoveLastWaypoint : CreateCustomCourseUiEvent

    data object Exit : CreateCustomCourseUiEvent

    data object CreateCustomCourseSuccess : CreateCustomCourseUiEvent

    data object CourseLengthTooShort : CreateCustomCourseUiEvent

    data object InvalidCourseName : CreateCustomCourseUiEvent

    data object DuplicateCourseName : CreateCustomCourseUiEvent

    data object UnauthorizedUser : CreateCustomCourseUiEvent

    data object UnknownError : CreateCustomCourseUiEvent
}
