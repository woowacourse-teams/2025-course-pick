package io.coursepick.coursepick.presentation.auth

import io.coursepick.coursepick.presentation.coursedetail.CourseReviewUiModel

sealed interface AuthFeature {
    data class ReportCourse(
        val courseId: String,
    ) : AuthFeature

    data class DeleteReview(
        val review: CourseReviewUiModel,
    ) : AuthFeature

    data class ReportReview(
        val review: CourseReviewUiModel,
    ) : AuthFeature

    data class WriteReview(
        val courseId: String,
    ) : AuthFeature

    data object CreateCustomCourse : AuthFeature

    data object CustomCourse : AuthFeature
}
