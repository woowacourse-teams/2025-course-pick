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

            val encodedName: String = java.net.URLEncoder.encode(destinationName, "UTF-8")

            return "https://map.naver.com/p/directions/" +
                "$originX,$originY,$ORIGIN_NAME,0,PLACE_POI/" +
                "$destinationX,$destinationY,$encodedName,0,PLACE_POI/" +
                "-/walk?c=16.00,0,0,0,dh"
        }

        private fun Coordinate.toWebMercatorPair(): Pair<Double, Double> {
            val longitude: Double = longitude.value
            val webMercatorX: Double = longitude * 20037508.34 / 180.0

            val latitude: Double = latitude.value
            var webMercatorY: Double =
                ln(tan((90.0 + latitude) * Math.PI / 360.0)) / (Math.PI / 180.0)

            webMercatorY = webMercatorY * 20037508.34 / 180.0
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
    }
}
