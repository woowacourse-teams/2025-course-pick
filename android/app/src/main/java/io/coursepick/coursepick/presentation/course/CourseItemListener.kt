package io.coursepick.coursepick.presentation.course

interface CourseItemListener {
    fun select(course: CourseItem)

    fun navigateToMap(course: CourseItem)
}
