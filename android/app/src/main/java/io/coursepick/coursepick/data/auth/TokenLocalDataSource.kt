package io.coursepick.coursepick.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.crypto.tink.Aead
import com.google.crypto.tink.subtle.Base64
import io.coursepick.coursepick.BuildConfig
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TokenLocalDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        private val aead: Aead,
    ) {
        private val tokenSecurity: String = BuildConfig.TOKEN_SECURITY
        private val accessToken: Preferences.Key<String> = stringPreferencesKey("access_token")

        suspend fun saveAccessToken(token: String) {
            val ciphertext = aead.encrypt(token.toByteArray(), tokenSecurity.toByteArray())
            val encryptedToken = Base64.encodeToString(ciphertext, Base64.NO_WRAP)
            dataStore.edit { preferences: MutablePreferences ->
                preferences[accessToken] = encryptedToken
            }
        }

        suspend fun accessToken(): String? {
            val encryptedToken = dataStore.data.first()[accessToken] ?: return null
            val decoded = Base64.decode(encryptedToken, Base64.NO_WRAP)
            return String(aead.decrypt(decoded, tokenSecurity.toByteArray()))
        }

        suspend fun clearAccessToken() {
            dataStore.edit { preferences: MutablePreferences ->
                preferences.remove(accessToken)
            }
        }
    }
