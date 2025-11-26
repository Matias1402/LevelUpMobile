package com.example.levelupmobile.ui.catalog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.data.local.entities.Product
import com.example.levelupmobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Estado interno con TODOS los productos que llegan de AWS
    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())

    // 2. Estados para filtros
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 3. Estado de Categorías (Se calcula automáticamente de la lista de productos)
    val categories: StateFlow<List<String>> = _allProducts
        .map { products ->
            products.map { it.category }.distinct().sorted()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 4. Lógica de Filtrado en Memoria (Combina productos, categoría y búsqueda)
    val products: StateFlow<List<Product>> = combine(
        _allProducts,
        _selectedCategory,
        _searchQuery
    ) { products, category, query ->
        products.filter { product ->
            val matchesCategory = category == null || product.category == category
            val matchesSearch = query.isEmpty() || product.name.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Inicialización: Cargar datos al abrir
    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getProducts()
                if (response.isSuccessful) {
                    _allProducts.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Buscar producto por código (Lo buscamos en la lista que ya bajamos)
    suspend fun getProductByCode(code: String): Product? {
        // Si la lista está vacía, intentamos cargarla primero
        if (_allProducts.value.isEmpty()) {
            try {
                val response = RetrofitClient.apiService.getProducts()
                if (response.isSuccessful) {
                    _allProducts.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // Buscamos en memoria
        return _allProducts.value.find { it.code == code }
    }
}