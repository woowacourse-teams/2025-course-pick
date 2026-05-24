package io.coursepick.coursepick.presentation.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import io.coursepick.coursepick.R

object CoursePickPreferences {
    private lateinit var preferences: SharedPreferences
    private lateinit var doNotShowNoticesKey: String

    fun init(context: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        doNotShowNoticesKey = context.getString(R.string.do_not_show_notices_key)
    }

    fun shouldShowNotice(id: String): Boolean {
        val doNotShowNoticeIds: Set<String?> =
            preferences.getStringSet(doNotShowNoticesKey, null) ?: return true
        return !doNotShowNoticeIds.contains(id)
    }

    fun setDoNotShowNotice(id: String) {
        preferences.edit {
            val doNotShowNoticeIds =
                preferences.getStringSet(doNotShowNoticesKey, null) ?: emptySet()
            putStringSet(doNotShowNoticesKey, doNotShowNoticeIds + id)
        }
    }

    fun removeInvalidNoticeIds(currentNoticeIds: Set<String>) {
        preferences.edit {
            val doNotShowNoticeIds =
                preferences.getStringSet(doNotShowNoticesKey, null) ?: emptySet()
            putStringSet(doNotShowNoticesKey, doNotShowNoticeIds.intersect(currentNoticeIds))
        }
    }
}
