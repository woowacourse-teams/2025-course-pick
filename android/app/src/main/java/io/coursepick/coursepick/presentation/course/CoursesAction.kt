package io.coursepick.coursepick.presentation.course

interface CoursesAction {
    fun searchThisArea()

    fun search()

    fun moveToCurrentLocation()

    fun copyClientId()

    fun showCourseColorDescription()

    fun clearQuery()
}
