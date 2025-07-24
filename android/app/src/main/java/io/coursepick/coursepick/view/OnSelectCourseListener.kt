package io.coursepick.coursepick.view

interface OnSelectCourseListener {
    fun select(course: CourseItem)

    fun navigate(course: CourseItem)
}
