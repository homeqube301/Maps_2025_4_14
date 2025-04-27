package com.mKanta.archivemaps.data.local

import android.util.Log
import androidx.core.content.edit
import com.mKanta.archivemaps.domain.model.NamedMarker
import kotlinx.serialization.json.Json

object MarkerLocalDataSource {
    fun saveMarkers(context: android.content.Context, markers: List<NamedMarker>) {
        val prefs = context.getSharedPreferences("markers", android.content.Context.MODE_PRIVATE)
        val json = Json.encodeToString(markers)
        prefs.edit {
            putString("marker_list", json)
        }
    }

    fun loadMarkers(context: android.content.Context): List<NamedMarker> {
        val prefs = context.getSharedPreferences("markers", android.content.Context.MODE_PRIVATE)
        val json = prefs.getString("marker_list", null) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            Log.e("loadMarkers", "JSONデコードに失敗: ${e.message}")
            Log.e("loadMarkers", "破損JSON: $json")
            emptyList()
        }
    }
}

