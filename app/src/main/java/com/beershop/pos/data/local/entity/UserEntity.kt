package com.beershop.pos.data.local.entity

import androidx.room.*
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val passwordHash: String,
    val displayName: String,
    val role: String, // OWNER, MANAGER, CASHIER
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null,
    val syncStatus: String = SyncStatus.PENDING,
    val syncId: String? = null
)

object SyncStatus {
    const val PENDING = "PENDING"
    const val SYNCED = "SYNCED"
    const val FAILED = "FAILED"
    const val NOT_SYNCED = "NOT_SYNCED"
}

object UserRole {
    const val OWNER = "OWNER"
    const val MANAGER = "MANAGER"
    const val CASHIER = "CASHIER"

    fun hasPermission(role: String, action: Action): Boolean {
        return when (action) {
            Action.MANAGE_USERS -> role == OWNER
            Action.MANAGE_PRODUCTS -> role in listOf(OWNER, MANAGER)
            Action.MANAGE_TABLES -> role in listOf(OWNER, MANAGER)
            Action.PROCESS_ORDERS -> role in listOf(OWNER, MANAGER, CASHIER)
            Action.PROCESS_PAYMENT -> role in listOf(OWNER, MANAGER, CASHIER)
            Action.VIEW_REPORTS -> role in listOf(OWNER, MANAGER)
            Action.MANAGE_SETTINGS -> role in listOf(OWNER, MANAGER)
            Action.DISCOUNT -> role in listOf(OWNER, MANAGER)
            Action.REOPEN_BILL -> role in listOf(OWNER, MANAGER)
            Action.VOID_ORDER -> role in listOf(OWNER, MANAGER)
        }
    }
}

enum class Action {
    MANAGE_USERS,
    MANAGE_PRODUCTS,
    MANAGE_TABLES,
    PROCESS_ORDERS,
    PROCESS_PAYMENT,
    VIEW_REPORTS,
    MANAGE_SETTINGS,
    DISCOUNT,
    REOPEN_BILL,
    VOID_ORDER
}
