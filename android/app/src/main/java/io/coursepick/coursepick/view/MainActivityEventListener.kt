package io.coursepick.coursepick.view

import com.google.android.material.navigation.NavigationView

interface MainActivityEventListener {
    val onMenuSelectedListener: NavigationView.OnNavigationItemSelectedListener

    fun onClickSearchThisAreaButton()

    fun onMenuButtonClicked()
}
