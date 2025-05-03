package com.mKanta.archivemaps.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey

object PreferenceKeys {
    val SHOW_LIST_INTRO = booleanPreferencesKey("show_list_intro")
    val SHOW_MAP_INTRO = booleanPreferencesKey("show_map_intro")
    val SHOW_DETAIL_INTRO = booleanPreferencesKey("show_detail_intro")
}
