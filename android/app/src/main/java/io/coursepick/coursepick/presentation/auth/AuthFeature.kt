package io.coursepick.coursepick.presentation.auth

import io.coursepick.coursepick.domain.course.Course

sealed interface AuthFeature {
    data class ReportCourse(
        val course: Course,
    ) : AuthFeature

    data object CreateCustomCourse : AuthFeature

    data object CustomCourse : AuthFeature
}
