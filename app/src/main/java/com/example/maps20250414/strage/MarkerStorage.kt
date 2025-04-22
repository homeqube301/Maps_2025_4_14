package com.example.maps20250414.strage

import android.util.Log
import com.example.maps20250414.model.NamedMarker
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun saveMarkers(context: android.content.Context, markers: List<NamedMarker>) {
    val prefs = context.getSharedPreferences("markers", android.content.Context.MODE_PRIVATE)
    val json = Json.encodeToString(markers)
    prefs.edit().putString("marker_list", json).apply()
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

