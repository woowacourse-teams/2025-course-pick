package io.coursepick.coursepick.view

import io.coursepick.coursepick.domain.Coordinate

class KakaoMapUrl {
    fun url(
        start: Coordinate,
        end: Coordinate,
        endName: String,
    ): String {
        val startName = "현재 위치"
        return "https://map.kakao.com/link/by/walk/" +
            "$startName,${start.latitude.value},${start.longitude.value}/$endName,${end.latitude.value},${end.longitude.value}"
    }
}
