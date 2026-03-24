package io.coursepick.coursepick.presentation.map

import android.view.ViewGroup

interface MapManagerFactory {
    fun create(container: ViewGroup): MapManager
}
