package io.coursepick.coursepick.presentation.course

import android.view.MenuItem

interface CoursesAction {
    fun searchThisArea()

    fun openMenu()

    fun navigate(item: MenuItem): Boolean

    fun search()

    fun moveToCurrentLocation()

    fun copyClientId()
}
