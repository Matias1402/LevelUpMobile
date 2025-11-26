package cl.duoc.levelupmobile.data.remote

import cl.duoc.levelupmobile.data.local.entities.CartItem
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth (Ya los tenías)
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // --- NUEVOS: CARRITO (Backend) ---

    @GET("api/cart/{userId}")
    suspend fun getCart(@Path("userId") userId: Int): Response<List<CartItem>>

    @POST("api/cart")
    suspend fun addToCart(@Body item: CartItem): Response<CartItem>

    @DELETE("api/cart/clear/{userId}")
    suspend fun clearCart(@Path("userId") userId: Int): Response<Void>
}