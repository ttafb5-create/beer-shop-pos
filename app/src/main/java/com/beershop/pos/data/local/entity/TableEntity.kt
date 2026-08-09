package com.beershop.pos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tables")
data class TableEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val tableNumber: String,       // e.g., "1", "A1", "VIP-1"
    val tableName: String = "",    // e.g., "Main Hall 1", "VIP Room"
    val capacity: Int = 4,
    val status: String = TableStatus.AVAILABLE,
    val currentOrderId: String? = null,
    val positionX: Float = 0f,     // For visual layout
    val positionY: Float = 0f,
    val zone: String = "Main",     // Zone/Area: "Main", "VIP", "Outdoor", etc.
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = SyncStatus.PENDING,
    val syncId: String? = null
)

object TableStatus {
    const val AVAILABLE = "AVAILABLE"
    const val OCCUPIED = "OCCUPIED"
    const val RESERVED = "RESERVED"
    const val HELD = "HELD"
    const val CLOSED = "CLOSED"
}
