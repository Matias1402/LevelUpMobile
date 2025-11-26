package cl.duoc.levelupmobile.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupmobile.data.local.datastore.PreferencesManager
import cl.duoc.levelupmobile.data.local.entities.CartItem
import cl.duoc.levelupmobile.data.remote.RetrofitClient
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

    val discount: StateFlow<Int> = subtotal.map { total ->
        // Aquí podrías agregar lógica para verificar si es alumno Duoc
        0
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val total: StateFlow<Int> = combine(subtotal, discount) { sub, disc ->
        sub - disc
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val currentUser = flow { emit(null) }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Al iniciar el ViewModel, cargamos los datos de la nube
    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = preferencesManager.getUserId()
                if (userId != -1) {
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

    // --- CORRECCIÓN PRINCIPAL: Ahora acepta 'quantity' ---
    fun addToCart(productCode: String, name: String, price: Int, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = preferencesManager.getUserId()
            if (userId != -1) {
                val newItem = CartItem(
                    productCode = productCode,
                    productName = name,
                    productPrice = price,
                    quantity = quantity, // Usamos la cantidad seleccionada
                    userId = userId.toLong() // Aseguramos que sea Long para el backend
                )
                // Enviamos al Backend
                val response = RetrofitClient.apiService.addToCart(newItem)
                if (response.isSuccessful) {
                    loadCart() // Recargamos la lista para ver el cambio
                }
            }
        }
    }

    // --- AGREGADO: Funciones necesarias para CartScreen ---

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // Reutilizamos addToCart porque en tu backend simple, guardar
            // un item existente suele actualizarlo (dependiendo de la lógica JPA)
            // Si quieres borrar al llegar a 0:
            if (newQuantity <= 0) {
                removeItem(cartItem)
            } else {
                addToCart(cartItem.productCode, cartItem.productName, cartItem.productPrice, newQuantity)
            }
        }
    }

    fun removeItem(cartItem: CartItem) {
        // NOTA: Como tu backend actual no tiene endpoint para borrar UN solo item,
        // por ahora solo recargamos el carrito.
        // Si quisieras borrar uno específico, necesitarías agregar @DELETE("api/cart/item/{id}") en Spring.
        // Para que la app no falle, dejaremos esta función recargando datos.
        loadCart()
    }

    fun clearCart() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = preferencesManager.getUserId()
            if (userId != -1) {
                RetrofitClient.apiService.clearCart(userId)
                loadCart() // Limpiamos la lista visualmente
            }
        }
    }
}