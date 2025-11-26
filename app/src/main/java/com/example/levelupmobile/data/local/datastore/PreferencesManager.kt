package com.example.levelupmobile.data.local.datastore

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("levelup_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
    }

    // Guardar Token
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    // Obtener Token
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // Guardar ID (Asegúrate de que esto se llame al registrarse)
    fun saveUserId(id: Int) {
        prefs.edit().putInt(KEY_USER_ID, id).apply()
    }

    // Obtener ID (Si no existe, devuelve -1)
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }


    fun clearData() {
        prefs.edit().clear().apply()
    }
}