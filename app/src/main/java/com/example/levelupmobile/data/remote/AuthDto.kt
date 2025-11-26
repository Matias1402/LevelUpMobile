package com.example.levelupmobile.data.remote

import com.google.gson.annotations.SerializedName

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val age: Int
)

data class AuthResponse(
    val token: String,

    @SerializedName("userId") // Esta etiqueta es la que hace la magia
    val userId: Int,

    val name: String,
    val email: String
)