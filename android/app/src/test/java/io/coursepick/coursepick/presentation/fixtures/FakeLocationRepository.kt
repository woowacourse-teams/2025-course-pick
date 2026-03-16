package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.fixture.COORDINATE_FIXTURE
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.domain.location.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocationRepository : LocationRepository {
    override val isCoarseLocationPermissionGranted: Boolean = true

    override val isFineLocationPermissionGranted: Boolean = true

    override val locationUpdates: Flow<Location?> = flowOf(Location.Fine(COORDINATE_FIXTURE))

    override suspend fun currentLocation(): Location = Location.Fine(COORDINATE_FIXTURE)
}
