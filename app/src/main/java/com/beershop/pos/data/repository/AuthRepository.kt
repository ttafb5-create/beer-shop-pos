package com.beershop.pos.data.repository

import com.beershop.pos.data.local.dao.UserDao
import com.beershop.pos.data.local.entity.UserEntity
import com.beershop.pos.data.local.entity.UserRole
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun login(username: String, password: String): Result<UserEntity> {
        val user = userDao.getUserByUsername(username)
            ?: return Result.failure(Exception("Invalid username or password"))

        val passwordHash = hashPassword(password)
        if (user.passwordHash != passwordHash) {
            return Result.failure(Exception("Invalid username or password"))
        }

        userDao.updateLastLogin(user.id)
        return Result.success(user.copy(lastLoginAt = System.currentTimeMillis()))
    }

    suspend fun createUser(
        username: String,
        password: String,
        displayName: String,
        role: String
    ): Result<UserEntity> {
        val existing = userDao.getUserByUsername(username)
        if (existing != null) {
            return Result.failure(Exception("Username already exists"))
        }

        val user = UserEntity(
            username = username,
            passwordHash = hashPassword(password),
            displayName = displayName,
            role = role
        )
        userDao.insertUser(user)
        return Result.success(user)
    }

    suspend fun updatePassword(userId: String, newPassword: String) {
        val user = userDao.getUserById(userId) ?: return
        userDao.updateUser(user.copy(
            passwordHash = hashPassword(newPassword),
            updatedAt = System.currentTimeMillis()
        ))
    }

    suspend fun getUsersByRole(role: String) = userDao.getUsersByRole(role)
    suspend fun getAllUsers() = userDao.getAllUsers()
    suspend fun deactivateUser(userId: String) = userDao.deactivateUser(userId)
    suspend fun hasPermission(role: String, action: com.beershop.pos.data.local.entity.Action) =
        UserRole.hasPermission(role, action)

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
