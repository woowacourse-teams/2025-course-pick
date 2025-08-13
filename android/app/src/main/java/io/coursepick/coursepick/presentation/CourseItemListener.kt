package io.coursepick.coursepick.presentation

interface CourseItemListener {
    fun select(course: CourseItem)

    fun navigateToMap(course: CourseItem)
}
