package com.example.booksy.data.local

import android.content.Context
import android.content.SharedPreferences


class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "AUTH_PREFS"
        private const val AUTH_TOKEN_KEY = "AUTH_TOKEN"
    }



    fun saveAuthToken(token: String) {
        prefs.edit().putString(AUTH_TOKEN_KEY, token).apply()
    }


    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN_KEY, null)
    }


    fun clearAuthToken() {
        prefs.edit().remove(AUTH_TOKEN_KEY).apply()
    }
}