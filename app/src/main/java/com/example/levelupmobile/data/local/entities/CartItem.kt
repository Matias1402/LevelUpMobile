package com.example.levelupmobile.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productCode: String,
    val productName: String,
    val productPrice: Int,
    val quantity: Int,
    val userId: Int
)