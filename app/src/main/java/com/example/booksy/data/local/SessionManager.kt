package com.example.booksy.data.local

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("booksy_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_AUTH_TOKEN = "auth_token"
        const val KEY_USER_NAME = "user_name"
        const val KEY_PHOTO_URI = "photo_uri"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun fetchUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun savePhotoUri(uri: Uri?) {
        prefs.edit().putString(KEY_PHOTO_URI, uri.toString()).apply()
    }

    fun fetchPhotoUri(): Uri? {
        val uriString = prefs.getString(KEY_PHOTO_URI, null)
        return if (uriString != null) Uri.parse(uriString) else null
    }

    fun clearAuthToken() {
        prefs.edit().clear().apply()
    }


    fun clearSession() {
        clearAuthToken()
    }
}