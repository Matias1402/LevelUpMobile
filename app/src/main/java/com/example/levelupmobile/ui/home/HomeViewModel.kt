package cl.duoc.levelupmobile.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupmobile.data.local.database.AppDatabase
import cl.duoc.levelupmobile.data.local.datastore.PreferencesManager
import cl.duoc.levelupmobile.data.local.entities.Product
import cl.duoc.levelupmobile.data.local.entities.User
import cl.duoc.levelupmobile.data.repository.ProductRepository
import cl.duoc.levelupmobile.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val productRepository = ProductRepository(database.productDao())
    private val userRepository = UserRepository(database.userDao())
    private val preferencesManager = PreferencesManager(application)

    val featuredProducts: StateFlow<List<Product>> = productRepository.getAllProducts()
        .map { products -> products.sortedByDescending { it.rating }.take(6) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories: StateFlow<List<String>> = productRepository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentUser: StateFlow<User?> = preferencesManager.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            userRepository.getUserById(userId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}