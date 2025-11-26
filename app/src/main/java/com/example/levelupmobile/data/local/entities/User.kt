package com.example.levelupmobile.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val age: Int,
    val isDuocStudent: Boolean = false,
    val levelUpPoints: Int = 0,
    val referralCode: String = "",
    val profileImageUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)