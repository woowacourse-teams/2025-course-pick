package io.coursepick.coursepick.domain.location

import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val isCoarseLocationPermissionGranted: Boolean

    val isFineLocationPermissionGranted: Boolean

    val locationUpdates: Flow<Location?>

    fun fetchCurrentLocation(
        onSuccess: (location: Location) -> Unit,
        onFailure: (exception: Exception) -> Unit,
    )

    fun startLocationUpdates(
        onUpdate: (location: Location) -> Unit,
        onFailure: (exception: Exception) -> Unit,
    )

    fun stopLocationUpdates()
}
