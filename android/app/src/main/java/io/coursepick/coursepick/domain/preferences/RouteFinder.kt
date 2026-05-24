package io.coursepick.coursepick.domain.preferences

sealed interface RouteFinder {
    data object None : RouteFinder

    data object Local : RouteFinder

    sealed interface ThirdParty : RouteFinder {
        data object KakaoMap : ThirdParty

        data object NaverMap : ThirdParty
    }
}
