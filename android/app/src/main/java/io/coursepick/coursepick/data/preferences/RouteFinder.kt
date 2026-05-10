package io.coursepick.coursepick.data.preferences

sealed interface RouteFinder {
    data object Local : RouteFinder

    sealed interface ThirdParty : RouteFinder {
        data object KakaoMap : ThirdParty

        data object NaverMap : ThirdParty
    }
}
