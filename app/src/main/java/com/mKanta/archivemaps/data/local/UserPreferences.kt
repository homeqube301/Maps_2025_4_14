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
            .map { it[PreferenceKeys.SHOW_LIST_INTRO] ?: true }

    suspend fun saveShowListIntro(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.SHOW_LIST_INTRO] = value
        }
    }
}
