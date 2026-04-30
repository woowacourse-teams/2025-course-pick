package io.coursepick.coursepick.data.preference

import io.coursepick.coursepick.domain.preference.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultUserPreferenceRepository
    @Inject
    constructor(
        private val dataStore: UserPreferenceDataStore,
    ) : UserPreferenceRepository {
        override val routeFinder: Flow<RouteFinder?> = dataStore.routeFinder

        override suspend fun setRouteFinder(routeFinder: RouteFinder?) {
            dataStore.setRouteFinder(routeFinder)
        }
    }
