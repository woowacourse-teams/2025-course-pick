package io.coursepick.coursepick.presentation.view.course

import android.view.MenuItem

interface CoursesAction {
    fun searchThisArea()

    fun openMenu()

    fun navigate(item: MenuItem): Boolean

    fun search()

    fun moveToCurrentLocation()
}
