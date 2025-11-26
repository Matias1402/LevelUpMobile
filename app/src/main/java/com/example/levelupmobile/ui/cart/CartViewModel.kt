package com.example.levelupmobile.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.data.local.datastore.PreferencesManager
import com.example.levelupmobile.data.local.entities.CartItem
import com.example.levelupmobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)

    // Estado del carrito (Lista vacía al inicio)
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Estados para los totales (Se calculan automáticamente cuando cambia el carrito)
    val subtotal: StateFlow<Int> = _cartItems.map { items ->
        items.sumOf { it.productPrice * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Nota: Por ahora simulamos el descuento.
    val discount: StateFlow<Int> = subtotal.map { total ->
        0 // Lógica de descuento pendiente o validación de alumno Duoc
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val total: StateFlow<Int> = combine(subtotal, discount) { sub, disc ->
        sub - disc
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Estado del usuario (opcional, inicializado en null)
    val currentUser = flow { emit(null) }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Al iniciar el ViewModel, cargamos los datos de la nube
    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = preferencesManager.getUserId()
                // Verificamos que el ID sea válido
                if (userId != -1 && userId != 0) {
                    val response = RetrofitClient.apiService.getCart(userId)
                    if (response.isSuccessful) {
                        // Actualizamos la lista con lo que llega de AWS
                        _cartItems.value = response.body() ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- FUNCIÓN PRINCIPAL DE AGREGAR ---
    fun addToCart(productCode: String, name: String, price: Int, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = preferencesManager.getUserId()

            // Validamos ID de usuario antes de enviar
            if (userId != -1 && userId != 0) {
                val newItem = CartItem(
                    productCode = productCode,
                    productName = name,
                    productPrice = price,
                    quantity = quantity, // Usamos la cantidad que viene de la pantalla
                    userId = userId // ¡CORREGIDO! Se envía como Int (sin .toLong())
                )

                try {
                    // Enviamos al Backend
                    val response = RetrofitClient.apiService.addToCart(newItem)
                    if (response.isSuccessful) {
                        loadCart() // Recargamos la lista para ver el cambio reflejado
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Actualizar cantidad (reutiliza addToCart ya que el backend sobreescribe/actualiza)
    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (newQuantity <= 0) {
                removeItem(cartItem)
            } else {
                addToCart(cartItem.productCode, cartItem.productName, cartItem.productPrice, newQuantity)
            }
        }
    }

    // Eliminar item (Por ahora recarga el carrito, idealmente se implementaría delete en backend)
    fun removeItem(cartItem: CartItem) {
        loadCart()
    }

    // Vaciar carrito completo
    fun clearCart() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = preferencesManager.getUserId()
            if (userId != -1 && userId != 0) {
                try {
                    RetrofitClient.apiService.clearCart(userId)
                    loadCart() // Limpiamos la lista visualmente
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}