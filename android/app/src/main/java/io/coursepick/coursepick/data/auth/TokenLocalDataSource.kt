package io.coursepick.coursepick.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TokenLocalDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        suspend fun saveAccessToken(token: String) {
            dataStore.edit { preferences: MutablePreferences ->
                preferences[ACCESS_TOKEN] = token
            }
        }

        suspend fun accessToken(): String? = dataStore.data.first()[ACCESS_TOKEN]

        suspend fun clearAccessToken() {
            dataStore.edit { preferences: MutablePreferences ->
                preferences.remove(ACCESS_TOKEN)
            }
        }

        companion object {
            private val ACCESS_TOKEN: Preferences.Key<String> = stringPreferencesKey("access_token")
        }
    }
