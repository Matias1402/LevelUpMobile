package com.example.levelupmobile.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.data.local.datastore.PreferencesManager
import com.example.levelupmobile.data.local.entities.User
import com.example.levelupmobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val userId = preferencesManager.getUserId()

            Log.e("PROFILE_DEBUG", "Cargando perfil... ID recuperado: $userId")

            if (userId != -1 && userId != 0) {
                try {
                    val response = RetrofitClient.apiService.getUserProfile(userId)
                    if (response.isSuccessful) {
                        Log.e("PROFILE_DEBUG", "Perfil descargado correctamente: ${response.body()?.name}")
                        _currentUser.value = response.body()
                    } else {
                        Log.e("PROFILE_DEBUG", "Error Servidor: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("PROFILE_DEBUG", "Error Conexión: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                Log.e("PROFILE_DEBUG", "ID inválido ($userId). No se llamó al servidor.")
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesManager.clearData()
        }
    }
}