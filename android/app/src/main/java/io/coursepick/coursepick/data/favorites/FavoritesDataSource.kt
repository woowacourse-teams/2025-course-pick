package io.coursepick.coursepick.data.favorites

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.coursepick.coursepick.di.Favorites
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesDataSource
    @Inject
    constructor(
        @Favorites private val dataStore: DataStore<Preferences>,
    ) {
        val courseIds: Flow<Set<String>> =
            dataStore.data
                .catch { exception: Throwable -> if (exception is IOException) emit(emptyPreferences()) else throw exception }
                .map { preferences: Preferences -> preferences[FAVORITE_COURSE_IDS_KEY] ?: emptySet() }

        suspend fun addFavorite(courseId: String) {
            dataStore.edit { preferences: MutablePreferences ->
                preferences[FAVORITE_COURSE_IDS_KEY] = preferences[FAVORITE_COURSE_IDS_KEY].orEmpty() + courseId
            }
        }

        suspend fun removeFavorite(courseId: String) {
            dataStore.edit { preferences: MutablePreferences ->
                preferences[FAVORITE_COURSE_IDS_KEY] = preferences[FAVORITE_COURSE_IDS_KEY].orEmpty() - courseId
            }
        }

        companion object {
            private val FAVORITE_COURSE_IDS_KEY: Preferences.Key<Set<String>> = stringSetPreferencesKey("favorited_courses_key")
        }
    }
