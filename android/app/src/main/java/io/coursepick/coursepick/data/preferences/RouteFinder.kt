package io.coursepick.coursepick.data.preferences

sealed interface RouteFinder {
    data object Local : RouteFinder

    data object KakaoMap : RouteFinder

    data object NaverMap : RouteFinder
}
