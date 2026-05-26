package io.coursepick.coursepick.presentation.auth

sealed interface AuthFeature {
    data class ReportCourse(
        val courseId: String,
    ) : AuthFeature

    data class SubmitReview(
        val courseId: String,
    ) : AuthFeature

    data object CreateCustomCourse : AuthFeature

    data object CustomCourse : AuthFeature
}
