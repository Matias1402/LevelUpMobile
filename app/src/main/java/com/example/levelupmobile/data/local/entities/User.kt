package com.example.levelupmobile.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val email: String,


    val password: String? = null,

    val age: Int,

    val isDuocStudent: Boolean = false,
    val levelUpPoints: Int = 0,

    val referralCode: String? = null,
    val profileImageUri: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)