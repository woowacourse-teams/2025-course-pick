package io.coursepick.coursepick.data.preference

import io.coursepick.coursepick.domain.preference.PreferencesRepository
import io.coursepick.coursepick.domain.preference.RouteFinder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultPreferencesRepository
    @Inject
    constructor(
        private val dataSource: PreferencesDataSource,
    ) : PreferencesRepository {
        override val routeFinder: Flow<RouteFinder> = dataSource.routeFinder

        override suspend fun setRouteFinder(routeFinder: RouteFinder) {
            dataSource.setRouteFinder(routeFinder)
        }
    }
