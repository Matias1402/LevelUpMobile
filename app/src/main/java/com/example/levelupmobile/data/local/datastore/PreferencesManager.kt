package cl.duoc.levelupmobile.data.local.datastore

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("LevelUpPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_AUTH_TOKEN = "auth_token" // Clave para el Token
    }

    // Guardar ID del usuario
    fun saveUserId(userId: Int) {
        sharedPreferences.edit().putInt(KEY_USER_ID, userId).apply()
    }

    // Sobrecarga por si el ID viene como Long desde el backend
    fun saveUserId(userId: Long) {
        sharedPreferences.edit().putInt(KEY_USER_ID, userId.toInt()).apply()
    }

    // Obtener ID del usuario
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    // --- ESTO ES LO QUE TE FALTABA ---

    // Guardar el Token JWT
    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    // Obtener el Token (lo usaremos para pedir productos)
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    // Borrar todo (Cerrar sesión)
    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }
}