package io.coursepick.coursepick.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.crypto.tink.Aead
import com.google.crypto.tink.subtle.Base64
import io.coursepick.coursepick.BuildConfig
import io.coursepick.coursepick.di.AuthDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TokenLocalDataSource
    @Inject
    constructor(
        @AuthDataStore private val dataStore: DataStore<Preferences>,
        private val aead: Aead,
    ) {
        suspend fun userId(): String? = dataStore.data.first()[userIdKey]

        suspend fun saveUserId(userId: String) {
            dataStore.edit { preferences: MutablePreferences -> preferences[userIdKey] = userId }
        }

        suspend fun accessToken(): String? {
            val encryptedToken: String = dataStore.data.first()[accessTokenKey] ?: return null
            val decoded: ByteArray = Base64.decode(encryptedToken, Base64.NO_WRAP)
            return String(aead.decrypt(decoded, tokenSecurity))
        }

        suspend fun saveAccessToken(token: String) {
            val ciphertext: ByteArray = aead.encrypt(token.toByteArray(), tokenSecurity)
            val encryptedToken: String = Base64.encodeToString(ciphertext, Base64.NO_WRAP)
            dataStore.edit { preferences: MutablePreferences ->
                preferences[accessTokenKey] = encryptedToken
            }
        }

        suspend fun clearCredentials() {
            dataStore.edit { preferences: MutablePreferences ->
                preferences.remove(userIdKey)
                preferences.remove(accessTokenKey)
            }
        }

        companion object {
            private val tokenSecurity: ByteArray = BuildConfig.TOKEN_SECURITY.toByteArray()
            private val userIdKey: Preferences.Key<String> = stringPreferencesKey("user_id")
            private val accessTokenKey: Preferences.Key<String> = stringPreferencesKey("access_token")
        }
    }
