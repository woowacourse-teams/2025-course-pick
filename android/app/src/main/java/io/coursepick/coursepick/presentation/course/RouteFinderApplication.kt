package io.coursepick.coursepick.presentation.course

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.core.net.toUri
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.preferences.RouteFinder

sealed class RouteFinderApplication(
    val routeFinder: RouteFinder,
    @get:StringRes val nameId: Int,
) {
    data object None : RouteFinderApplication(RouteFinder.None, R.string.selected_route_finder_application_entry_none)

    data object InApp : RouteFinderApplication(RouteFinder.Local, R.string.selected_route_finder_application_entry_in_app)

    sealed class ThirdParty(
        routeFinder: RouteFinder.ThirdParty,
        nameId: Int,
    ) : RouteFinderApplication(routeFinder, nameId) {
        abstract fun intent(
            context: Context,
            origin: Coordinate,
            originName: String,
            destination: Coordinate,
            destinationName: String,
        ): Intent

        data object KakaoMap : ThirdParty(RouteFinder.ThirdParty.KakaoMap, R.string.selected_route_finder_application_entry_kakao_map) {
            private const val KAKAO_MAP_PACKAGE_NAME = "net.daum.android.map"

            override fun intent(
                context: Context,
                origin: Coordinate,
                originName: String,
                destination: Coordinate,
                destinationName: String,
            ): Intent {
                val encodedOriginName: String = Uri.encode(originName)
                val encodedDestinationName: String = Uri.encode(destinationName)

                val uri =
                    runCatching {
                        context.packageManager.getPackageInfo(KAKAO_MAP_PACKAGE_NAME, 0)
                        "kakaomap://route?" +
                            "sn=$encodedOriginName&sp=${origin.latitude.value},${origin.longitude.value}&" +
                            "en=$encodedDestinationName&ep=${destination.latitude.value},${destination.longitude.value}&" +
                            "by=foot"
                    }.getOrElse {
                        "https://map.kakao.com/link/by/walk/" +
                            "$encodedOriginName,${origin.latitude.value},${origin.longitude.value}/" +
                            "$encodedDestinationName,${destination.latitude.value},${destination.longitude.value}/"
                    }.toUri()
                return Intent(Intent.ACTION_VIEW, uri)
            }
        }

        data object NaverMap : ThirdParty(RouteFinder.ThirdParty.NaverMap, R.string.selected_route_finder_application_entry_naver_map) {
            private const val NAVER_MAP_PACKAGE_NAME = "com.nhn.android.nmap"

            override fun intent(
                context: Context,
                origin: Coordinate,
                originName: String,
                destination: Coordinate,
                destinationName: String,
            ): Intent {
                val encodedOriginName: String = Uri.encode(originName)
                val encodedDestinationName: String = Uri.encode(destinationName)

                val uri =
                    runCatching {
                        context.packageManager.getPackageInfo(NAVER_MAP_PACKAGE_NAME, 0)
                        "nmap://route/walk?" +
                            "slat=${origin.latitude.value}&slng=${origin.longitude.value}&sname=$encodedOriginName&" +
                            "dlat=${destination.latitude.value}&dlng=${destination.longitude.value}&dname=$encodedDestinationName&"
                    }.getOrElse {
                        "https://map.naver.com/p/directions/" +
                            "${origin.longitude.value},${origin.latitude.value},$encodedOriginName/" +
                            "${destination.longitude.value},${destination.latitude.value},$encodedDestinationName/" +
                            "-/walk"
                    }.toUri()
                return Intent(Intent.ACTION_VIEW, uri)
            }
        }
    }

    companion object {
        val Entries = listOf(None, InApp, ThirdParty.KakaoMap, ThirdParty.NaverMap)
    }
}
