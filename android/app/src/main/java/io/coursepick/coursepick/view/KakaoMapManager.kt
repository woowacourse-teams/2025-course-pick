package io.coursepick.coursepick.view

import android.Manifest
import android.location.Location
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapGravity
import com.kakao.vectormap.MapView
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude

class KakaoMapManager(
    private val mapView: MapView,
    private val locationProvider: LocationProvider = LocationProvider(mapView.context),
) {
    private val lifecycleHandler = KakaoMapLifecycleHandler(mapView)
    private val cameraController = KakaoMapCameraController(mapView.context)
    private val drawer = KakaoMapDrawer(mapView.context)
    private val eventHandler = KakaoMapEventHandler()
    private var kakaoMap: KakaoMap? = null

    val cameraPosition get(): LatLng? = kakaoMap?.cameraPosition?.position

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun start(onMapReady: (Coordinate) -> Unit) {
        val offsetPx: Float =
            mapView.context.resources.getDimension(R.dimen.map_logo_position_offset)
        lifecycleHandler.start { map: KakaoMap ->
            kakaoMap = map
            map.logo?.setPosition(
                MapGravity.TOP or MapGravity.LEFT,
                offsetPx,
                offsetPx,
            )
            map.setPadding(
                0,
                0,
                0,
                mapView.context.resources.getDimensionPixelSize(R.dimen.main_bottom_sheet_peek_height),
            )
            showCurrentLocation()
            locationProvider.fetchCurrentLocation(
                onSuccess = { location: Location ->
                    onMapReady(
                        Coordinate(
                            Latitude(location.latitude),
                            Longitude(location.longitude),
                        ),
                    )
                },
                onFailure = {},
            )
        }
    }

    fun resume() = lifecycleHandler.resume()

    fun pause() = lifecycleHandler.pause()

    fun draw(courses: List<CourseItem>) {
        kakaoMap?.let { map: KakaoMap ->
            drawer.drawCourses(map, courses)
        }
    }

    fun showSearchPosition(coordinate: Coordinate) {
        kakaoMap?.let { map: KakaoMap ->
            drawer.showSearchPosition(map, coordinate)
        }
    }

    fun fitTo(course: CourseItem) {
        kakaoMap?.let { map: KakaoMap ->
            cameraController.fitTo(
                course,
                map,
            )
        }
    }

    fun setOnCourseClickListener(
        courses: List<CourseItem>,
        onClick: (CourseItem) -> Unit,
    ) {
        kakaoMap?.let { map: KakaoMap ->
            eventHandler.setOnCourseClickListener(map, courses) { course: CourseItem ->
                onClick(course)
            }
        }
    }

    fun setOnCameraMoveListener(onCameraMove: () -> Unit) {
        kakaoMap?.let { map: KakaoMap ->
            eventHandler.setOnCameraMoveListener(map) {
                onCameraMove()
            }
        }
    }

    fun moveTo(
        latitude: Double,
        longitude: Double,
    ) {
        moveTo(Latitude(latitude), Longitude(longitude))
    }

    private fun moveTo(
        latitude: Latitude,
        longitude: Longitude,
    ) {
        kakaoMap?.let { map: KakaoMap ->
            val location =
                Location("search").apply {
                    this.latitude = latitude.value
                    this.longitude = longitude.value
                }
            cameraController.moveTo(map, location)
        }
    }

    fun showSearchLocation(
        latitude: Latitude,
        longitude: Longitude,
    ) {
        kakaoMap?.let { map ->
            val location =
                Location("search").apply {
                    this.latitude = latitude.value
                    this.longitude = longitude.value
                }

            drawer.drawLabel(map, R.drawable.image_current_location, location)
            cameraController.moveTo(map, location)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun showCurrentLocation() {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                kakaoMap?.let { map: KakaoMap ->
                    drawer.showUserPosition(map, location)
                    cameraController.moveTo(map, location)
                }
            },
            onFailure = {
                Toast
                    .makeText(
                        mapView.context,
                        "현재 위치를 불러오지 못했습니다.",
                        Toast.LENGTH_SHORT,
                    ).show()
            },
        )
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startTrackingCurrentLocation() {
        locationProvider.startLocationUpdates(
            onUpdate = { location ->
                kakaoMap?.let { map: KakaoMap ->
                    drawer.showUserPosition(map, location)
                }
            },
            onError = {
                kakaoMap?.let { map: KakaoMap ->
                    drawer.removeAllLabels(map)
                }
            },
        )
    }

    fun stopTrackingCurrentLocation() {
        locationProvider.stopLocationUpdates()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun fetchCurrentLocation(
        onSuccess: (Latitude, Longitude) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        locationProvider.fetchCurrentLocation(
            onSuccess = { location: Location ->
                onSuccess(Latitude(location.latitude), Longitude(location.longitude))
            },
            onFailure = onFailure,
        )
    }
}
