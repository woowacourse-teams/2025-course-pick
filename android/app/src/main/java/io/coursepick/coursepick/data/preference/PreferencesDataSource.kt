package io.coursepick.coursepick.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.coursepick.coursepick.di.Settings
import io.coursepick.coursepick.domain.preference.RouteFinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesDataSource
    @Inject
    constructor(
        @Settings private val dataStore: DataStore<Preferences>,
    ) {
        val routeFinder: Flow<RouteFinder> =
            dataStore.data.map { preferences: Preferences ->
                when (preferences[ROUTE_FINDER_KEY]) {
                    ROUTE_FINDER_VALUE_LOCAL -> RouteFinder.Local
                    ROUTE_FINDER_VALUE_KAKAO_MAP -> RouteFinder.ThirdParty.KakaoMap
                    ROUTE_FINDER_VALUE_NAVER_MAP -> RouteFinder.ThirdParty.NaverMap
                    else -> RouteFinder.None
                }
            }

        suspend fun setRouteFinder(routeFinder: RouteFinder) {
            dataStore.edit { preferences: MutablePreferences ->
                preferences[ROUTE_FINDER_KEY] =
                    when (routeFinder) {
                        RouteFinder.None -> ROUTE_FINDER_VALUE_NONE
                        RouteFinder.Local -> ROUTE_FINDER_VALUE_LOCAL
                        RouteFinder.ThirdParty.KakaoMap -> ROUTE_FINDER_VALUE_KAKAO_MAP
                        RouteFinder.ThirdParty.NaverMap -> ROUTE_FINDER_VALUE_NAVER_MAP
                    }
            }
        }

        companion object {
            private val ROUTE_FINDER_KEY: Preferences.Key<String> = stringPreferencesKey("route_finder_key")
            private const val ROUTE_FINDER_VALUE_NONE = "none"
            private const val ROUTE_FINDER_VALUE_LOCAL = "local"
            private const val ROUTE_FINDER_VALUE_KAKAO_MAP = "kakao_map"
            private const val ROUTE_FINDER_VALUE_NAVER_MAP = "naver_map"
        }
    }
