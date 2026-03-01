package io.coursepick.coursepick.domain.location

interface LocationRepository {
    val hasLocationPermission: Boolean
    val hasCoarseLocationPermission: Boolean
    val hasFineLocationPermission: Boolean

    fun fetchCurrentLocation(
        onSuccess: (location: Location) -> Unit,
        onFailure: (exception: Exception) -> Unit,
    )

    fun startTrackingLocation(
        onUpdate: (location: Location) -> Unit,
        onFailure: (exception: Exception) -> Unit,
    )

    fun stopTrackingLocation()
}
