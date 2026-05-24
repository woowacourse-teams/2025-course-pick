package io.coursepick.coursepick.data.notice

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.coursepick.coursepick.di.Notice
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NoticeDataSource
    @Inject
    constructor(
        @Notice private val dataStore: DataStore<Preferences>,
    ) {
        suspend fun mutedNoticeIds(): Set<String> = dataStore.data.first()[MUTED_NOTICE_IDS_KEY].orEmpty()

        suspend fun addMutedNoticeId(noticeId: String) {
            dataStore.edit { preferences: MutablePreferences ->
                preferences[MUTED_NOTICE_IDS_KEY] = preferences[MUTED_NOTICE_IDS_KEY].orEmpty() + noticeId
            }
        }

        suspend fun updateMutedNoticeIds(noticeIds: Set<String>) {
            dataStore.edit { preferences: MutablePreferences ->
                preferences[MUTED_NOTICE_IDS_KEY] = noticeIds
            }
        }

        companion object {
            private val MUTED_NOTICE_IDS_KEY: Preferences.Key<Set<String>> =
                stringSetPreferencesKey("muted_notice_ids_key")
        }
    }
