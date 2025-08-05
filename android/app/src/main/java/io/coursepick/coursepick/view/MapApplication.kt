package io.coursepick.coursepick.view

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import io.coursepick.coursepick.domain.Coordinate
import kotlin.math.ln
import kotlin.math.tan

enum class MapApplication(
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
            val (originX: Double, originY: Double) = origin.toWebMercatorPair()
            val (destinationX: Double, destinationY: Double) = destination.toWebMercatorPair()
            return "https://map.naver.com/p/directions/" +
                "$originX,$originY,$ORIGIN_NAME/" +
                "$destinationX,$destinationY,$destinationName/" +
                "-/walk"
        }

        private fun Coordinate.toWebMercatorPair(): Pair<Double, Double> {
            val longitude: Double = longitude.value
            val webMercatorX: Double =
                longitude * WEB_MERCATOR_HALF_CIRCUMFERENCE / HALF_CIRCLE_DEGREES

            val latitude: Double = latitude.value
            var webMercatorY: Double =
                ln(tan((QUARTER_CIRCLE_DEGREES + latitude) * Math.PI / FULL_CIRCLE_DEGREES)) / (Math.PI / HALF_CIRCLE_DEGREES)

            webMercatorY = webMercatorY * WEB_MERCATOR_HALF_CIRCUMFERENCE / HALF_CIRCLE_DEGREES
            return Pair(webMercatorX, webMercatorY)
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
        private const val WEB_MERCATOR_HALF_CIRCUMFERENCE = 20037508.34
        private const val FULL_CIRCLE_DEGREES = 360.0
        private const val HALF_CIRCLE_DEGREES = 180.0
        private const val QUARTER_CIRCLE_DEGREES = 90.0
    }
}
