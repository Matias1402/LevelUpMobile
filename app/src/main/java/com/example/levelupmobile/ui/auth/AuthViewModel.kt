package cl.duoc.levelupmobile.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupmobile.data.local.database.AppDatabase
import cl.duoc.levelupmobile.data.local.datastore.PreferencesManager
import cl.duoc.levelupmobile.data.local.entities.User
import cl.duoc.levelupmobile.data.repository.UserRepository
import cl.duoc.levelupmobile.utils.ValidationUtils
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao())
    private val preferencesManager = PreferencesManager(application)

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val emailValidation = ValidationUtils.validateEmail(email)
                if (!emailValidation.isValid) {
                    _loginResult.value = Result.failure(Exception(emailValidation.errorMessage))
                    return@launch
                }

                val passwordValidation = ValidationUtils.validatePassword(password)
                if (!passwordValidation.isValid) {
                    _loginResult.value = Result.failure(Exception(passwordValidation.errorMessage))
                    return@launch
                }

                val user = repository.login(email, password)
                if (user != null) {
                    preferencesManager.saveUserId(user.id)
                    _loginResult.value = Result.success(user)
                } else {
                    _loginResult.value = Result.failure(Exception("Credenciales incorrectas"))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }

    fun register(name: String, email: String, age: String, password: String, referralCode: String = "") {
        viewModelScope.launch {
            try {
                val nameValidation = ValidationUtils.validateName(name)
                if (!nameValidation.isValid) {
                    _registerResult.value = Result.failure(Exception(nameValidation.errorMessage))
                    return@launch
                }

                val emailValidation = ValidationUtils.validateEmail(email)
                if (!emailValidation.isValid) {
                    _registerResult.value = Result.failure(Exception(emailValidation.errorMessage))
                    return@launch
                }

                val ageValidation = ValidationUtils.validateAge(age)
                if (!ageValidation.isValid) {
                    _registerResult.value = Result.failure(Exception(ageValidation.errorMessage))
                    return@launch
                }

                val passwordValidation = ValidationUtils.validatePassword(password)
                if (!passwordValidation.isValid) {
                    _registerResult.value = Result.failure(Exception(passwordValidation.errorMessage))
                    return@launch
                }

                val existingUser = repository.getUserByEmail(email)
                if (existingUser != null) {
                    _registerResult.value = Result.failure(Exception("El correo ya está registrado"))
                    return@launch
                }

                val isDuoc = ValidationUtils.isDuocEmail(email)
                val user = User(
                    name = name,
                    email = email,
                    password = password,
                    age = age.toInt(),
                    isDuocStudent = isDuoc,
                    referralCode = referralCode
                )

                repository.insertUser(user)

                val message = if (isDuoc) {
                    "¡Registro exitoso! Tienes 20% de descuento de por vida 🎉"
                } else {
                    "¡Registro exitoso! Bienvenido a Level-Up Gamer"
                }

                _registerResult.value = Result.success(message)

            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            }
        }
    }
}