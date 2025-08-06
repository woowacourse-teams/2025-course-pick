package io.coursepick.coursepick.view

interface CourseItemListener {
    fun select(course: CourseItem)

    fun navigateToMap(course: CourseItem)
}
