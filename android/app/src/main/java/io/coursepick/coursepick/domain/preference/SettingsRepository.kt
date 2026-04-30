package io.coursepick.coursepick.domain.preference

import io.coursepick.coursepick.data.preference.RouteFinder
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val routeFinder: Flow<RouteFinder?>

    suspend fun setRouteFinder(routeFinder: RouteFinder?)
}
