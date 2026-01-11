package io.coursepick.coursepick.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.crypto.tink.Aead
import com.google.crypto.tink.subtle.Base64
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TokenLocalDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        private val aead: Aead,
    ) {
        suspend fun saveAccessToken(token: String) {
            val ciphertext = aead.encrypt(token.toByteArray(), TOKEN_SECURITY.toByteArray())
            val encryptedToken = Base64.encodeToString(ciphertext, Base64.NO_WRAP)
            dataStore.edit { preferences: MutablePreferences ->
                preferences[ACCESS_TOKEN] = encryptedToken
            }
        }

        suspend fun accessToken(): String? {
            val encryptedToken = dataStore.data.first()[ACCESS_TOKEN] ?: return null
            val decoded = Base64.decode(encryptedToken, Base64.NO_WRAP)
            return String(aead.decrypt(decoded, TOKEN_SECURITY.toByteArray()))
        }

        suspend fun clearAccessToken() {
            dataStore.edit { preferences: MutablePreferences ->
                preferences.remove(ACCESS_TOKEN)
            }
        }

        companion object {
            private val ACCESS_TOKEN: Preferences.Key<String> = stringPreferencesKey("access_token")
            private const val TOKEN_SECURITY: String = "token_security"
        }
    }
