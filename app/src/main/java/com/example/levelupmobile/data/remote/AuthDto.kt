package cl.duoc.levelupmobile.data.remote

// Lo que enviamos al hacer Login
data class LoginRequest(val email: String, val password: String)

// Lo que enviamos al registrarnos
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val age: Int
)

// Lo que el Backend nos responde (incluye el Token)
data class AuthResponse(
    val token: String,
    val userId: Int,
    val name: String,
    val email: String
)