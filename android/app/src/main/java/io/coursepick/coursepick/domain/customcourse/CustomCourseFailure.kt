package io.coursepick.coursepick.domain.customcourse

sealed interface CustomCourseFailure {
    data object InvalidCourseName : CustomCourseFailure

    data object UnauthorizedUser : CustomCourseFailure

    data object Unknown : CustomCourseFailure
}
