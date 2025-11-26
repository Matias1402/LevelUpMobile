package com.example.levelupmobile.data.local.database.dao

import androidx.room.*
import com.example.levelupmobile.data.local.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Int): Flow<User?>

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("UPDATE users SET levelUpPoints = levelUpPoints + :points WHERE id = :userId")
    suspend fun addPoints(userId: Int, points: Int)
}