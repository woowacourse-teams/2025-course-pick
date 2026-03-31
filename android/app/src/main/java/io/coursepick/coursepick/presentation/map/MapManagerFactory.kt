package io.coursepick.coursepick.presentation.map

import android.view.ViewGroup

interface MapManagerFactory {
    suspend fun create(container: ViewGroup): MapManager
}
