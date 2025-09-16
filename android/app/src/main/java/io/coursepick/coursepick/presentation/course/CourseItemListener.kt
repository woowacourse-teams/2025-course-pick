package io.coursepick.coursepick.presentation.course

interface CourseItemListener {
    fun select(course: CourseItem)

    fun navigateToCourse(course: CourseItem)
}
