package com.mKanta.archivemaps.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.serialization.json.Json

class SessionStore(
    context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)

    fun save(session: UserSession) {
        val json = Json.Default.encodeToString(session)
        prefs.edit { putString("session_json", json) }
    }

    fun load(): UserSession? {
        val json = prefs.getString("session_json", null) ?: return null
        return Json.Default.decodeFromString(UserSession.Companion.serializer(), json)
    }

    fun clear() {
        prefs.edit { remove("session_json") }
    }
}
