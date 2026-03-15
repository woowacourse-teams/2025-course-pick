package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.fixture.COORDINATE_FIXTURE
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.domain.location.LocationRepository

class FakeLocationRepository : LocationRepository {
    override val isCoarseLocationPermissionGranted: Boolean = true

    override val isFineLocationPermissionGranted: Boolean = true

    override fun fetchCurrentLocation(
        onSuccess: (location: Location) -> Unit,
        onFailure: (exception: Exception) -> Unit,
    ) {
        onSuccess(Location.Fine(COORDINATE_FIXTURE))
    }

    override fun startLocationUpdates(
        onUpdate: (location: Location) -> Unit,
        onFailure: (exception: Exception) -> Unit,
    ) {
        onUpdate(Location.Fine(COORDINATE_FIXTURE))
    }

    override fun stopLocationUpdates() {}
}
