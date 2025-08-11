package io.coursepick.coursepick.view

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import io.coursepick.coursepick.util.ClientId
import java.util.UUID

class ClientUuid(
    context: Context,
) : ClientId {
    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    override val value =
        preferences.getString(CLIENT_ID_KEY, null) ?: run {
            val uuid: String = UUID.randomUUID().toString()
            preferences.edit {
                putString(CLIENT_ID_KEY, uuid)
            }
            uuid
        }

    companion object {
        private const val CLIENT_ID_KEY = "globally_unique_identifier"
    }
}
