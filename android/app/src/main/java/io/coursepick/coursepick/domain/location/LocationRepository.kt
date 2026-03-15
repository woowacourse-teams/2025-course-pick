package io.coursepick.coursepick.domain.location

interface LocationRepository {
    val isCoarseLocationPermissionGranted: Boolean

    val isFineLocationPermissionGranted: Boolean

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
