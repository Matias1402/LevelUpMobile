package cl.duoc.levelupmobile.utils

import java.util.regex.Pattern

object ValidationUtils {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult(false, "El correo es requerido")
            !Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches() ->
                ValidationResult(false, "Formato de correo inválido")
            else -> ValidationResult(true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult(false, "La contraseña es requerida")
            password.length < 6 -> ValidationResult(false, "Mínimo 6 caracteres")
            else -> ValidationResult(true)
        }
    }

    fun validateAge(age: String): ValidationResult {
        return when {
            age.isEmpty() -> ValidationResult(false, "La edad es requerida")
            age.toIntOrNull() == null -> ValidationResult(false, "Edad inválida")
            age.toInt() < 18 -> ValidationResult(false, "Debes ser mayor de 18 años")
            age.toInt() > 120 -> ValidationResult(false, "Edad no válida")
            else -> ValidationResult(true)
        }
    }

    fun validateName(name: String): ValidationResult {
        return when {
            name.isEmpty() -> ValidationResult(false, "El nombre es requerido")
            name.length < 2 -> ValidationResult(false, "Nombre muy corto")
            !name.all { it.isLetter() || it.isWhitespace() } ->
                ValidationResult(false, "Solo letras y espacios")
            else -> ValidationResult(true)
        }
    }

    fun isDuocEmail(email: String): Boolean {
        return email.endsWith("@duocuc.cl", ignoreCase = true)
    }

    fun validateRut(rut: String): ValidationResult {
        val cleanRut = rut.replace(".", "").replace("-", "")
        if (cleanRut.length < 8) {
            return ValidationResult(false, "RUT inválido")
        }
        return ValidationResult(true)
    }
}