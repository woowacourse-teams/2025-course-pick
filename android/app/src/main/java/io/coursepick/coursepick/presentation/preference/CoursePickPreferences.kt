package io.coursepick.coursepick.presentation.preference

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.routefinder.RouteFinderApplication

object CoursePickPreferences {
    private lateinit var preferences: SharedPreferences
    private lateinit var selectedRouteFinderApplicationKey: String
    private lateinit var kakaoMap: String
    private lateinit var naverMap: String
    private lateinit var none: String

    private val sharedPreferencesChangeListener =
        OnSharedPreferenceChangeListener { _, key: String? ->
            Logger.log(
                Logger.Event.PreferenceChange(key.toString()),
                "new preference" to preferences.getString(key, null).toString(),
            )
        }

    fun init(context: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.registerOnSharedPreferenceChangeListener(sharedPreferencesChangeListener)

        selectedRouteFinderApplicationKey =
            context.getString(R.string.selected_route_finder_application_key)
        kakaoMap = context.getString(R.string.selected_route_finder_application_value_kakao)
        naverMap = context.getString(R.string.selected_route_finder_application_value_naver)
        none = context.getString(R.string.selected_route_finder_application_value_none)
    }

    var selectedRouteFinder: RouteFinderApplication?
        get() =
            when (preferences.getString(selectedRouteFinderApplicationKey, null)) {
                kakaoMap -> RouteFinderApplication.KAKAO_MAP
                naverMap -> RouteFinderApplication.NAVER_MAP
                else -> null
            }
        set(mapApplication) {
            preferences.edit {
                putString(
                    selectedRouteFinderApplicationKey,
                    mapApplication.serialized,
                )
            }
        }

    private val RouteFinderApplication?.serialized: String
        get() =
            when (this) {
                RouteFinderApplication.KAKAO_MAP -> kakaoMap
                RouteFinderApplication.NAVER_MAP -> naverMap
                null -> none
            }
}
