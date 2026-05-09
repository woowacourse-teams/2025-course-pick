package io.coursepick.coursepick.data.preferences

import io.coursepick.coursepick.domain.preferences.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultPreferencesRepository
    @Inject
    constructor(
        private val dataStore: PreferencesDataStore,
    ) : PreferencesRepository {
        override val routeFinder: Flow<RouteFinder?> = dataStore.routeFinder

        override suspend fun setRouteFinder(routeFinder: RouteFinder?) {
            dataStore.setRouteFinder(routeFinder)
        }
    }
