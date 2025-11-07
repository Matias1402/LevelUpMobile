package cl.duoc.levelupmobile.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupmobile.data.local.database.AppDatabase
import cl.duoc.levelupmobile.data.local.datastore.PreferencesManager
import cl.duoc.levelupmobile.data.local.entities.CartItem
import cl.duoc.levelupmobile.data.local.entities.User
import cl.duoc.levelupmobile.data.repository.CartRepository
import cl.duoc.levelupmobile.data.repository.UserRepository
import cl.duoc.levelupmobile.utils.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val cartRepository = CartRepository(database.cartDao())
    private val userRepository = UserRepository(database.userDao())
    private val preferencesManager = PreferencesManager(application)

    private val userId: Flow<Int?> = preferencesManager.userIdFlow

    val cartItems: StateFlow<List<CartItem>> = userId
        .filterNotNull()
        .flatMapLatest { id ->
            cartRepository.getCartItems(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentUser: StateFlow<User?> = userId
        .filterNotNull()
        .flatMapLatest { id ->
            userRepository.getUserById(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val subtotal: StateFlow<Int> = cartItems
        .map { items ->
            items.sumOf { it.productPrice * it.quantity }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val discount: StateFlow<Int> = combine(subtotal, currentUser) { total, user ->
        if (user?.isDuocStudent == true) {
            (total * Constants.DUOC_DISCOUNT).toInt()
        } else {
            0
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val total: StateFlow<Int> = combine(subtotal, discount) { sub, disc ->
        sub - disc
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                cartRepository.updateCartItem(cartItem.copy(quantity = newQuantity))
            } else {
                cartRepository.removeFromCart(cartItem)
            }
        }
    }

    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch {
            cartRepository.removeFromCart(cartItem)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            userId.firstOrNull()?.let { id ->
                cartRepository.clearCart(id)
            }
        }
    }
}