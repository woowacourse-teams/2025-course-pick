package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.runtime.Composable
import io.coursepick.coursepick.domain.course.Course

data class CustomCourseItem(
    val course: Course,
    val selected: Boolean,
)
