package io.coursepick.coursepick.data.preference

sealed interface RouteFinder {
    data object Local : RouteFinder

    sealed interface ThirdParty : RouteFinder {
        data object KakaoMap : ThirdParty

        data object NaverMap : ThirdParty
    }
}
