package io.coursepick.coursepick.presentation.auth

import io.coursepick.coursepick.presentation.course.CourseItem

sealed interface AuthFeature {
    data class ReportCourse(
        val course: CourseItem,
    ) : AuthFeature

    data object CustomCourse : AuthFeature
}
