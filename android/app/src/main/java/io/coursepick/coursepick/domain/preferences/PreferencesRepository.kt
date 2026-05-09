package io.coursepick.coursepick.domain.preferences

import io.coursepick.coursepick.data.preferences.RouteFinder
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val routeFinder: Flow<RouteFinder?>

    suspend fun setRouteFinder(routeFinder: RouteFinder?)
}
