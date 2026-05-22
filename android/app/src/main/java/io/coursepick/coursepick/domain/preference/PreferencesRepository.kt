package io.coursepick.coursepick.domain.preference

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val routeFinder: Flow<RouteFinder>

    suspend fun setRouteFinder(routeFinder: RouteFinder)
}
