package io.coursepick.coursepick.presentation.view.course

import io.coursepick.coursepick.presentation.model.course.CourseItem

interface CourseItemListener {
    fun select(course: CourseItem)

    fun navigateToMap(course: CourseItem)
}
