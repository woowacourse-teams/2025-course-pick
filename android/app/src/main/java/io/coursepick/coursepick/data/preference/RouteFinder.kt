package io.coursepick.coursepick.data.preference

sealed interface RouteFinder {
    data object Local : RouteFinder

    data object KakaoMap : RouteFinder

    data object NaverMap : RouteFinder
}
