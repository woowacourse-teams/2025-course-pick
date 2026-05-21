package io.coursepick.coursepick.presentation.coursedetail

sealed interface CourseDetailEvent {
    object NoNetwork : CourseDetailEvent

    object ReportCourseSuccess : CourseDetailEvent

    object CourseAlreadyReported : CourseDetailEvent

    object ReportCourseUnauthorizedUser : CourseDetailEvent

    object ReportCourseUnknownFailure : CourseDetailEvent
}
