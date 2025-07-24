package io.coursepick.coursepick.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationProvider(
    private val context: Context,
) {
    private val locationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    private val locationRequest =
        LocationRequest
            .Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                LOCATION_REQUEST_INTERVAL,
            ).build()

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun fetchCurrentLocation(
        onSuccess: (Location) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        if (!locationManager.isLocationEnabled) {
            onFailure(IllegalStateException("위치 설정이 꺼져있습니다."))
            return
        }

        if (!hasLocationPermission) {
            onFailure(IllegalStateException("현재 위치를 불러올 권한이 없습니다."))
            return
        }

        locationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location ->
                onSuccess(location)
            }.addOnFailureListener { exception: Exception ->
                onFailure(exception)
            }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startLocationUpdates(
        onUpdate: (Location) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        stopLocationUpdates()
        if (!hasLocationPermission) {
            onError(IllegalStateException("현재 위치를 불러올 권한이 없습니다."))
            return
        }

        val locationCallback = LocationCallback(onUpdate, onError)
        this.locationCallback = locationCallback

        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )
    }

    fun stopLocationUpdates() {
        locationCallback?.let { locationCallback: LocationCallback ->
            locationClient.removeLocationUpdates(locationCallback)
        }
        locationCallback = null
    }

    private fun LocationCallback(
        onUpdate: (Location) -> Unit,
        onError: (Exception) -> Unit,
    ): LocationCallback =
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location: Location -> onUpdate(location) }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    onError(IllegalStateException("현재 위치를 사용할 수 없습니다."))
                }
            }
        }

    private val hasLocationPermission: Boolean
        get() =
            hasFineLocationPermission || hasCoarseLocationPermission

    private val hasCoarseLocationPermission: Boolean
        get() =
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private val hasFineLocationPermission: Boolean
        get() =
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val LOCATION_REQUEST_INTERVAL = 1000L
    }
}
