package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.preferences.PreferencesRepository
import io.coursepick.coursepick.domain.preferences.RouteFinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePreferencesRepository(
    initialRouteFinder: RouteFinder = RouteFinder.None,
) : PreferencesRepository {
    private val _routeFinder = MutableStateFlow(initialRouteFinder)
    override val routeFinder: Flow<RouteFinder> get() = _routeFinder.asStateFlow()

    override suspend fun setRouteFinder(routeFinder: RouteFinder) {
        _routeFinder.value = routeFinder
    }
}
