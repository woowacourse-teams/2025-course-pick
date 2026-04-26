package io.coursepick.coursepick.presentation.map.naver

import com.naver.maps.geometry.LatLng
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude

fun LatLng.toCoordinate(): Coordinate = Coordinate(Latitude(latitude), Longitude(longitude))

fun Coordinate.toLatLng(): LatLng = LatLng(latitude.value, longitude.value)
