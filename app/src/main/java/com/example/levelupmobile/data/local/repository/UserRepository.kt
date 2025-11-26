package com.example.levelupmobile.data.local.repository

import com.example.levelupmobile.data.local.database.dao.UserDao
import com.example.levelupmobile.data.local.entities.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    fun getUserById(userId: Int): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun addPoints(userId: Int, points: Int) {
        userDao.addPoints(userId, points)
    }
}