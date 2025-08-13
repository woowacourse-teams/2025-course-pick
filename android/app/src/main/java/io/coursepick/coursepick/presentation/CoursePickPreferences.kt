package io.coursepick.coursepick.presentation

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import io.coursepick.coursepick.R

object CoursePickPreferences {
    private lateinit var preferences: SharedPreferences
    private lateinit var selectedMapApplicationKey: String
    private lateinit var kakaoMap: String
    private lateinit var naverMap: String
    private lateinit var none: String

    fun init(context: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        selectedMapApplicationKey = context.getString(R.string.selected_map_application_key)
        kakaoMap = context.getString(R.string.selected_map_application_value_kakao)
        naverMap = context.getString(R.string.selected_map_application_value_naver)
        none = context.getString(R.string.selected_map_application_value_none)
    }

    var selectedMapApplication: MapApplication?
        get() =
            when (preferences.getString(selectedMapApplicationKey, null)) {
                kakaoMap -> MapApplication.KAKAO_MAP
                naverMap -> MapApplication.NAVER_MAP
                else -> null
            }
        set(mapApplication) {
            preferences.edit {
                putString(
                    selectedMapApplicationKey,
                    mapApplication.serialized,
                )
            }
        }

    private val MapApplication?.serialized: String
        get() =
            when (this) {
                MapApplication.KAKAO_MAP -> kakaoMap
                MapApplication.NAVER_MAP -> naverMap
                null -> none
            }
}
