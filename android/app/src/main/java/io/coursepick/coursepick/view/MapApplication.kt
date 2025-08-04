package io.coursepick.coursepick.view

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import io.coursepick.coursepick.domain.Coordinate
import timber.log.Timber

enum class MapApplication {
    KAKAO_MAP {
        override fun navigationUrl(
            origin: Coordinate,
            destination: Coordinate,
            destinationName: String,
        ): String =
            "https://map.kakao.com/link/by/walk/" +
                "현위치,${origin.latitude.value},${origin.longitude.value}/" +
                "$destinationName,${destination.latitude.value},${destination.longitude.value}/"
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
        Timber.e(navigationUrl(origin, destination, destinationName))
        val intent =
            Intent(Intent.ACTION_VIEW, navigationUrl(origin, destination, destinationName).toUri())
        context.startActivity(intent)
    }
}
