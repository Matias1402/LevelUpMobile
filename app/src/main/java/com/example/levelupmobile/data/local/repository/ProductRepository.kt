package com.example.levelupmobile.data.local.repository

import com.example.levelupmobile.data.local.database.dao.ProductDao
import com.example.levelupmobile.data.local.entities.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
    }

    fun getProductsByCategory(category: String): Flow<List<Product>> {
        return productDao.getProductsByCategory(category)
    }

    suspend fun getProductByCode(code: String): Product? {
        return productDao.getProductByCode(code)
    }

    fun searchProducts(query: String): Flow<List<Product>> {
        return productDao.searchProducts(query)
    }

    fun getAllCategories(): Flow<List<String>> {
        return productDao.getAllCategories()
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }
}