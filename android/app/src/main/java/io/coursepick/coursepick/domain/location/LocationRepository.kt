package io.coursepick.coursepick.domain.location

import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val isCoarseLocationPermissionGranted: Boolean

    val isFineLocationPermissionGranted: Boolean

    val locationUpdates: Flow<Location?>

    suspend fun currentLocation(): Location?
}
