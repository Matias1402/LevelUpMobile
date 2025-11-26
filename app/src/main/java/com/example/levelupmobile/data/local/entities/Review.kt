package com.example.levelupmobile.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productCode: String,
    val userId: Int,
    val userName: String,
    val rating: Float,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)