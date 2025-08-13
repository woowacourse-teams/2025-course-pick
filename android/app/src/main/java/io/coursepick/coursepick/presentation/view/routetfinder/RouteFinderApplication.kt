package io.coursepick.coursepick.presentation.view.routetfinder

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import io.coursepick.coursepick.domain.Coordinate
import kotlin.math.ln
import kotlin.math.tan

enum class RouteFinderApplication(
    val appName: String,
) {
    KAKAO_MAP("카카오맵") {
        override fun navigationUrl(
            origin: Coordinate,
            destination: Coordinate,
            destinationName: String,
        ): String =
            "https://map.kakao.com/link/by/walk/" +
                "$ORIGIN_NAME,${origin.latitude.value},${origin.longitude.value}/" +
                "$destinationName,${destination.latitude.value},${destination.longitude.value}/"
    },

    NAVER_MAP("네이버 지도") {
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
    },
    ;

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

    companion object {
        private const val ORIGIN_NAME = "현위치"
        private const val EARTH_RADIUS_METERS = 6378137
    }
}
