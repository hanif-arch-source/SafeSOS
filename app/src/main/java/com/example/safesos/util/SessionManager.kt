package com.example.safesos.util

import android.content.Context

/**
 * Stores the logged-in user's id locally so the app can restore the session
 * on next launch instead of always starting at Login.
 */
class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("safesos_session", Context.MODE_PRIVATE)

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        return if (id == -1) null else id
    }

    fun clear() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    companion object {
        private const val KEY_USER_ID = "logged_in_user_id"
    }
}
