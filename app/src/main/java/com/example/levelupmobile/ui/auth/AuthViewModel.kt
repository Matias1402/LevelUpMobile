package cl.duoc.levelupmobile.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupmobile.data.local.datastore.PreferencesManager
import cl.duoc.levelupmobile.data.local.entities.User
import cl.duoc.levelupmobile.data.remote.LoginRequest
import cl.duoc.levelupmobile.data.remote.RegisterRequest
import cl.duoc.levelupmobile.data.remote.RetrofitClient
import cl.duoc.levelupmobile.utils.ValidationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Ya no usamos AppDatabase ni UserRepository local aquí
    private val preferencesManager = PreferencesManager(application)

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) { // Ejecutamos en hilo IO (Red)
            try {
                // Validaciones locales (se mantienen igual)
                val emailValidation = ValidationUtils.validateEmail(email)
                if (!emailValidation.isValid) {
                    _loginResult.postValue(Result.failure(Exception(emailValidation.errorMessage)))
                    return@launch
                }

                val passwordValidation = ValidationUtils.validatePassword(password)
                if (!passwordValidation.isValid) {
                    _loginResult.postValue(Result.failure(Exception(passwordValidation.errorMessage)))
                    return@launch
                }

                // 2. LLAMADA AL BACKEND CON RETROFIT
                val request = LoginRequest(email = email, password = password)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful) {
                    val authResponse = response.body()

                    if (authResponse != null) {
                        // 3. Guardar Token y ID
                        preferencesManager.saveToken(authResponse.token)
                        preferencesManager.saveUserId(authResponse.userId)

                        // 4. Creamos un objeto User temporal para que la UI no se rompa
                        // (Aunque la data real ahora viene del backend)
                        val user = User(
                            id = authResponse.userId,
                            name = authResponse.name,
                            email = authResponse.email,
                            password = "", // No guardamos la pass en RAM por seguridad
                            age = 0 // El login simple no devuelve edad, pero no importa para navegar al Home
                        )
                        _loginResult.postValue(Result.success(user))
                    } else {
                        _loginResult.postValue(Result.failure(Exception("Respuesta vacía del servidor")))
                    }
                } else {
                    // Error del servidor (401, 404, etc)
                    val errorMsg = if (response.code() == 403 || response.code() == 401) {
                        "Credenciales incorrectas"
                    } else {
                        "Error en el servidor: ${response.code()}"
                    }
                    _loginResult.postValue(Result.failure(Exception(errorMsg)))
                }

            } catch (e: Exception) {
                // Error de conexión (Backend apagado o IP 10.0.2.2 mal configurada)
                _loginResult.postValue(Result.failure(Exception("Error de conexión: ${e.message}")))
            }
        }
    }

    fun register(name: String, email: String, age: String, password: String, referralCode: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Validaciones locales
                val nameValidation = ValidationUtils.validateName(name)
                if (!nameValidation.isValid) {
                    _registerResult.postValue(Result.failure(Exception(nameValidation.errorMessage)))
                    return@launch
                }
                // ... (resto de validaciones igual) ...
                val emailValidation = ValidationUtils.validateEmail(email)
                if (!emailValidation.isValid) {
                    _registerResult.postValue(Result.failure(Exception(emailValidation.errorMessage)))
                    return@launch
                }
                val ageValidation = ValidationUtils.validateAge(age)
                if (!ageValidation.isValid) {
                    _registerResult.postValue(Result.failure(Exception(ageValidation.errorMessage)))
                    return@launch
                }

                // 5. LLAMADA DE REGISTRO AL BACKEND
                val request = RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    age = age.toInt()
                )

                val response = RetrofitClient.apiService.register(request)

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse != null) {
                        // El backend nos devuelve el token inmediatamente al registrarse
                        preferencesManager.saveToken(authResponse.token)
                        preferencesManager.saveUserId(authResponse.userId)

                        val isDuoc = ValidationUtils.isDuocEmail(email)
                        val message = if (isDuoc) {
                            "¡Registro exitoso! Tienes 20% de descuento 🎉"
                        } else {
                            "¡Registro exitoso! Bienvenido"
                        }
                        _registerResult.postValue(Result.success(message))
                    }
                } else {
                    // Si el backend dice "Email en uso" (Status 500 o 400 según tu controller)
                    _registerResult.postValue(Result.failure(Exception("Error al registrar: ${response.message()}")))
                }

            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(e))
            }
        }
    }
}