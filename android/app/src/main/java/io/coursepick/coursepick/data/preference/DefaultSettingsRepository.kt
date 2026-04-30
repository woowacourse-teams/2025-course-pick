package io.coursepick.coursepick.data.preference

import io.coursepick.coursepick.domain.preference.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultSettingsRepository
    @Inject
    constructor(
        private val dataStore: SettingsDataStore,
    ) : SettingsRepository {
        override val routeFinder: Flow<RouteFinder?> = dataStore.routeFinder

        override suspend fun setRouteFinder(routeFinder: RouteFinder?) {
            dataStore.setRouteFinder(routeFinder)
        }
    }
