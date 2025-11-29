package com.example.levelupmobile.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.data.local.datastore.PreferencesManager
import com.example.levelupmobile.data.local.entities.User
import com.example.levelupmobile.data.remote.ApiService
import com.example.levelupmobile.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProfileViewModel(
    application: Application,
    private val preferencesManager: PreferencesManager = PreferencesManager(application), // Valor por defecto
    private val apiService: ApiService = RetrofitClient.apiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(application) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        // Usamos ioDispatcher en lugar de Dispatchers.IO directamente
        viewModelScope.launch(ioDispatcher) {
            _isLoading.value = true
            val userId = preferencesManager.getUserId()

            Log.e("PROFILE_DEBUG", "Cargando perfil... ID recuperado: $userId")

            if (userId != -1 && userId != 0) {
                try {
                    val response = apiService.getUserProfile(userId)
                    if (response.isSuccessful) {
                        _currentUser.value = response.body()
                    } else {
                        Log.e("PROFILE_DEBUG", "Error Servidor: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("PROFILE_DEBUG", "Error Conexión: ${e.message}")
                }
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch(ioDispatcher) {
            preferencesManager.clearData()
        }
    }
}