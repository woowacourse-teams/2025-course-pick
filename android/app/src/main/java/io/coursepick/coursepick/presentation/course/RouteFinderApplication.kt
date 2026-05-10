package io.coursepick.coursepick.presentation.course

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.core.net.toUri
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.preferences.RouteFinder
import kotlin.math.ln
import kotlin.math.tan

sealed class RouteFinderApplication(
    val routeFinder: RouteFinder,
    @get:StringRes val nameId: Int,
) {
    data object InApp : RouteFinderApplication(RouteFinder.Local, R.string.route_finder_application_entry_in_app)

    sealed class ThirdParty(
        routeFinder: RouteFinder.ThirdParty,
        nameId: Int,
    ) : RouteFinderApplication(routeFinder, nameId) {
        abstract fun intent(
            origin: Coordinate,
            originName: String,
            destination: Coordinate,
            destinationName: String,
        ): Intent

        data object KakaoMap : ThirdParty(RouteFinder.ThirdParty.KakaoMap, R.string.route_finder_application_entry_kakao_map) {
            override fun intent(
                origin: Coordinate,
                originName: String,
                destination: Coordinate,
                destinationName: String,
            ): Intent {
                val encodedOriginName: String = Uri.encode(originName)
                val encodedDestinationName: String = Uri.encode(destinationName)

                val uri =
                    (
                        "https://map.kakao.com/link/by/walk/" +
                            "$encodedOriginName,${origin.latitude.value},${origin.longitude.value}/" +
                            "$encodedDestinationName,${destination.latitude.value},${destination.longitude.value}/"
                    ).toUri()
                return Intent(Intent.ACTION_VIEW, uri)
            }
        }

        data object NaverMap : ThirdParty(RouteFinder.ThirdParty.NaverMap, R.string.route_finder_application_entry_naver_map) {
            private const val EARTH_RADIUS_METERS = 6_378_137

            override fun intent(
                origin: Coordinate,
                originName: String,
                destination: Coordinate,
                destinationName: String,
            ): Intent {
                val (originX: Double, originY: Double) = origin.toWebMercator()
                val encodedOriginName: String = Uri.encode(originName)

                val (destinationX: Double, destinationY: Double) = destination.toWebMercator()
                val encodedDestinationName: String = Uri.encode(destinationName)

                val uri =
                    (
                        "https://map.naver.com/p/directions/" +
                            "$originX,$originY,$encodedOriginName/" +
                            "$destinationX,$destinationY,$encodedDestinationName/" +
                            "-/walk"
                    ).toUri()
                return Intent(Intent.ACTION_VIEW, uri)
            }

            private fun Coordinate.toWebMercator(): Pair<Double, Double> {
                val longitudeRadians: Double = Math.toRadians(longitude.value)
                val latitudeRadians: Double = Math.toRadians(latitude.value)

                val x: Double = EARTH_RADIUS_METERS * longitudeRadians
                val y: Double = EARTH_RADIUS_METERS * ln(tan(Math.PI / 4 + latitudeRadians / 2))

                return x to y
            }
        }
    }

    companion object {
        val Entries = listOf(InApp, ThirdParty.KakaoMap, ThirdParty.NaverMap)
    }
}
