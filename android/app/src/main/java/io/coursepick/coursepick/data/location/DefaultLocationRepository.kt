package io.coursepick.coursepick.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.domain.location.LocationRepository
import io.coursepick.coursepick.presentation.map.kakao.toCoordinate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.tasks.await

class DefaultLocationRepository(
    private val context: Context,
) : LocationRepository {
    private val locationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest =
        LocationRequest
            .Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                LOCATION_REQUEST_INTERVAL_MS,
            ).build()

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val locationRefreshUpdates: MutableSharedFlow<Location> = MutableSharedFlow()

    @SuppressLint("MissingPermission")
    private val locationCallbackUpdates: Flow<Location?> =
        callbackFlow {
            if (!locationManager.isLocationEnabled || !isCoarseLocationPermissionGranted) {
                trySend(null)
                close()
                return@callbackFlow
            }

            val locationCallback =
                LocationCallback(onUpdate = ::trySend, onFailure = { trySend(null) })
            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper(),
            )
            awaitClose { locationClient.removeLocationUpdates(locationCallback) }
        }

    override val isCoarseLocationPermissionGranted: Boolean
        get() = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    override val isFineLocationPermissionGranted: Boolean
        get() = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    override val locationUpdates: Flow<Location?> =
        merge(locationRefreshUpdates, locationCallbackUpdates)

    @SuppressLint("MissingPermission")
    override suspend fun currentLocation(): Location? {
        if (!locationManager.isLocationEnabled || !isCoarseLocationPermissionGranted) return null

        val androidLocation: android.location.Location =
            runCatching {
                locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            }.getOrNull() ?: return null

        val location: Location =
            if (isFineLocationPermissionGranted) {
                Location.Fine(androidLocation.toCoordinate())
            } else {
                Location.Coarse(
                    androidLocation.toCoordinate(),
                    Distance(androidLocation.accuracy),
                )
            }
        locationRefreshUpdates.emit(location)
        return location
    }

    private fun LocationCallback(
        onUpdate: (location: Location) -> Unit,
        onFailure: (Exception) -> Unit,
    ): LocationCallback =
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location: android.location.Location ->
                    val coordinate = location.toCoordinate()
                    onUpdate(
                        if (isFineLocationPermissionGranted) {
                            Location.Fine(coordinate)
                        } else {
                            Location.Coarse(
                                coordinate,
                                Distance(location.accuracy),
                            )
                        },
                    )
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!locationManager.isLocationEnabled) {
                    onFailure(IllegalStateException("현재 위치를 사용할 수 없습니다."))
                }
            }
        }

    companion object {
        private const val LOCATION_REQUEST_INTERVAL_MS = 1_000L
    }
}
