package com.example.levelupmobile.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.data.local.datastore.PreferencesManager
import com.example.levelupmobile.data.local.entities.Product
import com.example.levelupmobile.data.local.entities.User
import com.example.levelupmobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Mantenemos tu PreferencesManager (ya sin flows complejos)
    private val preferencesManager = PreferencesManager(application)

    // Estado para Productos Destacados (Cargados desde AWS)
    private val _featuredProducts = MutableStateFlow<List<Product>>(emptyList())
    val featuredProducts: StateFlow<List<Product>> = _featuredProducts.asStateFlow()

    // Estado para Categorías
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    // Estado para el Usuario
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. CARGAR PRODUCTOS DESDE AWS
                // Usamos RetrofitClient en lugar de productRepository local
                val response = RetrofitClient.apiService.getProducts()

                if (response.isSuccessful) {
                    val allProducts = response.body() ?: emptyList()

                    // Filtramos en memoria los mejores calificados (Top 6)
                    _featuredProducts.value = allProducts
                        .sortedByDescending { it.rating }
                        .take(6)

                    // Extraemos las categorías únicas
                    _categories.value = allProducts
                        .map { it.category }
                        .distinct()
                }

                // 2. Aquí quitamos la lógica de 'userIdFlow' que te daba error.
                // Si necesitas cargar el usuario, puedes usar preferencesManager.getUserId()
                // y llamar a una API, pero por ahora lo dejamos limpio para que compile.

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}