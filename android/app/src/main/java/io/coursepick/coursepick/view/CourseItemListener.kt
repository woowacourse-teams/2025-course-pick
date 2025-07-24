package io.coursepick.coursepick.view

interface CourseItemListener {
    fun select(course: CourseItem)

    fun navigate(course: CourseItem)
}
