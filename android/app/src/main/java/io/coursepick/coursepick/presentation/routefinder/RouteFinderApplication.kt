package io.coursepick.coursepick.presentation.routefinder

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.net.toUri
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import kotlinx.parcelize.Parcelize
import kotlin.math.ln
import kotlin.math.tan

@Parcelize
sealed interface RouteFinderApplication : Parcelable {
    @get:StringRes
    val nameId: Int

    object InApp : RouteFinderApplication {
        @StringRes
        override val nameId: Int = R.string.selected_route_finder_application_entry_in_app
    }

    @Parcelize
    object KakaoMap :
        ThirdParty(R.string.selected_route_finder_application_entry_kakao_map),
        Parcelable {
        override fun navigationUrl(
            origin: Coordinate,
            destination: Coordinate,
            destinationName: String,
        ): String =
            "https://map.kakao.com/link/by/walk/" +
                "$ORIGIN_NAME,${origin.latitude.value},${origin.longitude.value}/" +
                "$destinationName,${destination.latitude.value},${destination.longitude.value}/"
    }

    @Parcelize
    object NaverMap :
        ThirdParty(R.string.selected_route_finder_application_entry_naver_map),
        Parcelable {
        override fun navigationUrl(
            origin: Coordinate,
            destination: Coordinate,
            destinationName: String,
        ): String {
            val (originX: Double, originY: Double) = origin.toWebMercator()
            val (destinationX: Double, destinationY: Double) = destination.toWebMercator()

            return "https://map.naver.com/p/directions/" +
                "$originX,$originY,$ORIGIN_NAME/" +
                "$destinationX,$destinationY,$destinationName/" +
                "-/walk"
        }

        private fun Coordinate.toWebMercator(): Pair<Double, Double> {
            val longitudeRadians: Double = Math.toRadians(longitude.value)
            val latitudeRadians: Double = Math.toRadians(latitude.value)

            val x: Double = EARTH_RADIUS_METERS * longitudeRadians
            val y: Double = EARTH_RADIUS_METERS * ln(tan(Math.PI / 4 + latitudeRadians / 2))

            return x to y
        }
    }

    sealed class ThirdParty(
        @StringRes override val nameId: Int,
    ) : RouteFinderApplication {
        protected abstract fun navigationUrl(
            origin: Coordinate,
            destination: Coordinate,
            destinationName: String,
        ): String

        fun launch(
            context: Context,
            origin: Coordinate,
            destination: Coordinate,
            destinationName: String,
        ) {
            runCatching {
                val intent =
                    Intent(
                        Intent.ACTION_VIEW,
                        navigationUrl(origin, destination, destinationName).toUri(),
                    )

                context.startActivity(intent)
            }.onFailure {
                Toast.makeText(context, "길찾기 앱을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        val ALL = listOf(InApp, KakaoMap, NaverMap)
        private const val ORIGIN_NAME = "현위치"
        private const val EARTH_RADIUS_METERS = 6378137
    }
}
