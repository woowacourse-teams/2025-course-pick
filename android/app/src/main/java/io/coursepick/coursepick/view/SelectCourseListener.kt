package io.coursepick.coursepick.view

interface SelectCourseListener {
    fun select(course: CourseItem)

    fun navigate(course: CourseItem)
}
