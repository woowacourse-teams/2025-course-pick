package io.coursepick.coursepick.presentation.view.map.kakao

import android.location.Location
import com.kakao.vectormap.LatLng
import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude

fun LatLng.toCoordinate(): Coordinate = Coordinate(Latitude(latitude), Longitude(longitude))

fun Coordinate.toLatLng(): LatLng = LatLng.from(latitude.value, longitude.value)

fun Location.toLatLng(): LatLng = LatLng.from(latitude, longitude)
