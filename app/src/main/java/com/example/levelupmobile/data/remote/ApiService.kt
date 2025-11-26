package com.example.levelupmobile.data.remote

import com.example.levelupmobile.data.local.entities.CartItem
import com.example.levelupmobile.data.local.entities.Product
import com.example.levelupmobile.data.local.entities.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTENTICACIÓN ---
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // --- CARRITO (Backend AWS) ---
    @GET("api/cart/{userId}")
    suspend fun getCart(@Path("userId") userId: Int): Response<List<CartItem>>

    @POST("api/cart")
    suspend fun addToCart(@Body item: CartItem): Response<CartItem>

    @DELETE("api/cart/clear/{userId}")
    suspend fun clearCart(@Path("userId") userId: Int): Response<Void>

    // --- PRODUCTOS ---
    @GET("api/products")
    suspend fun getProducts(): Response<List<Product>>

    // --- PERFIL (CORREGIDO) ---
    // Antes pedía email, ahora pide ID para coincidir con tu Backend y ViewModel
    @GET("api/auth/profile/{id}")
    suspend fun getUserProfile(@Path("id") id: Int): Response<User>
}