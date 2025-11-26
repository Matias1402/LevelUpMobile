package com.example.levelupmobile.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val code: String,
    val category: String,
    val name: String,
    val price: Int,
    val description: String,
    val imageUrl: String = "",
    val stock: Int = 10,
    val rating: Float = 0f,
    val reviewCount: Int = 0
)