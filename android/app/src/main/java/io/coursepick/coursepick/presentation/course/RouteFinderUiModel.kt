package io.coursepick.coursepick.presentation.course

import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.net.toUri
import io.coursepick.coursepick.R
import io.coursepick.coursepick.data.preference.RouteFinder
import io.coursepick.coursepick.domain.course.Coordinate
import kotlin.math.ln
import kotlin.math.tan

sealed interface RouteFinderUiModel {
    val routeFinder: RouteFinder

    @get:StringRes
    val nameId: Int

    data object InApp : RouteFinderUiModel {
        override val routeFinder: RouteFinder = RouteFinder.Local
        override val nameId: Int = R.string.selected_route_finder_application_entry_in_app
    }

    sealed interface ThirdParty : RouteFinderUiModel {
        fun intent(
            origin: Coordinate,
            originName: String,
            destination: Coordinate,
            destinationName: String,
        ): Intent

        data object KakaoMap : ThirdParty {
            override val routeFinder: RouteFinder = RouteFinder.KakaoMap
            override val nameId: Int = R.string.selected_route_finder_application_entry_kakao_map

            override fun intent(
                origin: Coordinate,
                originName: String,
                destination: Coordinate,
                destinationName: String,
            ): Intent {
                val uri =
                    """
                    https://map.kakao.com/link/by/walk/
                    $originName,${origin.latitude.value},${origin.longitude.value}/
                    $destinationName,${destination.latitude.value},${destination.longitude.value}/
                    """.trimIndent().toUri()
                return Intent(Intent.ACTION_VIEW, uri)
            }
        }

        data object NaverMap : ThirdParty {
            override val routeFinder: RouteFinder = RouteFinder.NaverMap
            override val nameId: Int = R.string.selected_route_finder_application_entry_naver_map

            private const val EARTH_RADIUS_METERS = 6_378_137

            override fun intent(
                origin: Coordinate,
                originName: String,
                destination: Coordinate,
                destinationName: String,
            ): Intent {
                val (originX: Double, originY: Double) = origin.toWebMercator()
                val (destinationX: Double, destinationY: Double) = destination.toWebMercator()
                val uri =
                    """
                    https://map.naver.com/p/directions/
                    $originX,$originY,$originName/
                    $destinationX,$destinationY,$destinationName/
                    -/walk
                    """.trimIndent().toUri()
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
