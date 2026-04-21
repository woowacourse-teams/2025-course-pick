package io.coursepick.coursepick.domain.customcourse

sealed interface SubmitCourseResult {
    data object Success : SubmitCourseResult

    sealed interface Failure : SubmitCourseResult {
        data object InvalidCourseName : Failure

        data object UnauthorizedUser : Failure

        data object Unknown : Failure
    }
}
