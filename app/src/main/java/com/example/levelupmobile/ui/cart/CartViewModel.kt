package com.example.levelupmobile.ui.cart

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.data.local.database.AppDatabase
import com.example.levelupmobile.data.local.datastore.PreferencesManager
import com.example.levelupmobile.data.local.entities.CartItem
import com.example.levelupmobile.data.local.repository.CartRepository
import com.example.levelupmobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)

    private val database = AppDatabase.getDatabase(application)

    private val repository = CartRepository(database.cartDao(), RetrofitClient.apiService)

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val subtotal: StateFlow<Int> = _cartItems.map { items ->
        items.sumOf { it.productPrice * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val discount: StateFlow<Int> = subtotal.map {
        0 // Lógica pendiente
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val total: StateFlow<Int> = combine(subtotal, discount) { sub, disc ->
        sub - disc
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)


    init {
        initializeCartData()
    }

    private fun initializeCartData() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = preferencesManager.getUserId()

            if (userId != -1 && userId != 0) {
                repository.syncCartFromCloud(userId)

                repository.getCartItems(userId).collect { items ->
                    _cartItems.value = items
                }
            }
        }
    }

    fun addToCart(productCode: String, name: String, price: Int, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = preferencesManager.getUserId()

            if (userId != -1 && userId != 0) {
                val newItem = CartItem(
                    productCode = productCode,
                    productName = name,
                    productPrice = price,
                    quantity = quantity,
                    userId = userId
                )

                repository.addToCart(newItem)
            } else {
                Log.e("CartViewModel", "Usuario no logueado, no se puede agregar al carrito")
            }
        }
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (newQuantity <= 0) {
                removeItem(cartItem)
            } else {
                repository.updateCartItem(cartItem.copy(quantity = newQuantity))
            }
        }
    }

    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeFromCart(cartItem)
        }
    }

    fun clearCart() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = preferencesManager.getUserId()
            if (userId != -1) {
                repository.clearCart(userId)
            }
        }
    }
}