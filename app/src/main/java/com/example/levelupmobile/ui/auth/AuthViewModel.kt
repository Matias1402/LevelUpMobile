package com.example.levelupmobile.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.data.local.datastore.PreferencesManager
import com.example.levelupmobile.data.remote.LoginRequest
import com.example.levelupmobile.data.remote.RegisterRequest
import com.example.levelupmobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)

    private val _loginResult = MutableLiveData<Result<Boolean>>()
    val loginResult: LiveData<Result<Boolean>> = _loginResult

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.e("AUTH_DEBUG", "Intentando Login con: $email")
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))

                if (response.isSuccessful) {
                    response.body()?.let { auth ->
                        Log.e("AUTH_DEBUG", "Login Éxito. ID Recibido: ${auth.userId}")

                        // Guardamos
                        preferencesManager.saveToken(auth.token)
                        preferencesManager.saveUserId(auth.userId)

                        // Verificamos si se guardó
                        val savedId = preferencesManager.getUserId()
                        Log.e("AUTH_DEBUG", "ID Verificado en Memoria: $savedId")

                        _loginResult.postValue(Result.success(true))
                    }
                } else {
                    Log.e("AUTH_DEBUG", "Error Login: ${response.code()} - ${response.message()}")
                    _loginResult.postValue(Result.failure(Exception("Error Login: ${response.code()}")))
                }
            } catch (e: Exception) {
                Log.e("AUTH_DEBUG", "Excepción Login: ${e.message}")
                _loginResult.postValue(Result.failure(e))
            }
        }
    }

    fun register(name: String, email: String, age: String, password: String, referralCode: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.e("AUTH_DEBUG", "Intentando Registro...")
                val request = RegisterRequest(name, email, password, age.toIntOrNull() ?: 18)
                val response = RetrofitClient.apiService.register(request)

                if (response.isSuccessful) {
                    response.body()?.let { auth ->
                        Log.e("AUTH_DEBUG", "Registro Éxito. ID Recibido: ${auth.userId}")

                        // Guardamos
                        preferencesManager.saveToken(auth.token)
                        preferencesManager.saveUserId(auth.userId)

                        _registerResult.postValue(Result.success("Registro exitoso"))
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    Log.e("AUTH_DEBUG", "Error Registro Backend: $errorMsg")
                    _registerResult.postValue(Result.failure(Exception("Error Registro: $errorMsg")))
                }
            } catch (e: Exception) {
                Log.e("AUTH_DEBUG", "Excepción Registro: ${e.message}")
                _registerResult.postValue(Result.failure(e))
            }
        }
    }
}