package com.example.levelupmobile.data.local.repository

import com.example.levelupmobile.data.local.database.dao.CartDao
import com.example.levelupmobile.data.local.entities.CartItem
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    fun getCartItems(userId: Int): Flow<List<CartItem>> {
        return cartDao.getCartItems(userId)
    }

    suspend fun addToCart(cartItem: CartItem) {
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
        cartDao.clearCart(userId)
    }

    fun getCartItemCount(userId: Int): Flow<Int?> {
        return cartDao.getCartItemCount(userId)
    }
}