package io.coursepick.coursepick.view

import android.view.MenuItem

interface MainAction {
    fun searchThisArea()

    fun openMenu()

    fun navigate(item: MenuItem): Boolean

    fun search()

    fun moveToCurrentLocation()
}
