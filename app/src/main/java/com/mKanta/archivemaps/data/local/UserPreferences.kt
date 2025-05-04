package com.mKanta.archivemaps.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
    private val context: Context,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val showListIntroFlow: Flow<Boolean> =
        context.dataStore.data
            .map { it[PreferenceKeys.SHOW_LIST_INTRO] != false }

    val showMapIntroFlow: Flow<Boolean> =
        context.dataStore.data
            .map { it[PreferenceKeys.SHOW_MAP_INTRO] != false }

    val showDetailIntroFlow: Flow<Boolean> =
        context.dataStore.data
            .map { it[PreferenceKeys.SHOW_DETAIL_INTRO] != false }

    suspend fun saveShowListIntro(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.SHOW_LIST_INTRO] = value
        }
    }

    suspend fun saveShowMapIntro(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.SHOW_MAP_INTRO] = value
        }
    }

    suspend fun saveShowDetailIntro(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.SHOW_DETAIL_INTRO] = value
        }
    }
}
