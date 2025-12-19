package com.example.levelupmobile.data.local.repository

import android.util.Log
import com.example.levelupmobile.data.local.database.dao.CartDao
import com.example.levelupmobile.data.local.entities.CartItem
import com.example.levelupmobile.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class CartRepository(
    private val cartDao: CartDao,
    private val apiService: ApiService
) {


    fun getCartItems(userId: Int): Flow<List<CartItem>> {
        return cartDao.getCartItems(userId)
    }


    suspend fun syncCartFromCloud(userId: Int) {
        try {
            val response = apiService.getCart(userId)
            if (response.isSuccessful && response.body() != null) {
                val cloudItems = response.body()!!

                cloudItems.forEach { item ->
                    val existing = cartDao.getCartItemByProduct(item.productCode, item.userId)
                    if (existing == null) {
                        cartDao.insertCartItem(item)
                    } else {
                    }
                }
                Log.d("CartRepo", "Carrito sincronizado desde AWS: ${cloudItems.size} productos")
            }
        } catch (e: Exception) {
            Log.e("CartRepo", "Error sincronizando carrito: ${e.message}")
        }
    }

    suspend fun addToCart(cartItem: CartItem) {
        try {
            val response = apiService.addToCart(cartItem)
            if (response.isSuccessful) {
                Log.d("CartRepo", "Producto enviado a Spring Boot/AWS exitosamente")
            } else {
                Log.e("CartRepo", "Error en servidor: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("CartRepo", "Sin conexión a internet, guardando solo local: ${e.message}")
        }


        val existing = cartDao.getCartItemByProduct(cartItem.productCode, cartItem.userId)
        if (existing != null) {
            cartDao.updateCartItem(existing.copy(quantity = existing.quantity + cartItem.quantity))
        } else {
            cartDao.insertCartItem(cartItem)
        }
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        cartDao.updateCartItem(cartItem)
    }

    suspend fun removeFromCart(cartItem: CartItem) {

        cartDao.deleteCartItem(cartItem)
    }

    suspend fun clearCart(userId: Int) {
        try {
            val response = apiService.clearCart(userId)
            if (response.isSuccessful) Log.d("CartRepo", "Carrito vaciado en AWS")
        } catch (e: Exception) {
            Log.e("CartRepo", "No se pudo vaciar en AWS: ${e.message}")
        }

        // 2. Borrar Local
        cartDao.clearCart(userId)
    }

    fun getCartItemCount(userId: Int): Flow<Int?> {
        return cartDao.getCartItemCount(userId)
    }
}