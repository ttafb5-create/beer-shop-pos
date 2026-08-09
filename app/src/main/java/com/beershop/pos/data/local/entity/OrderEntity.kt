package com.beershop.pos.data.local.entity

import androidx.room.*
import java.util.UUID

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = TableEntity::class,
            parentColumns = ["id"],
            childColumns = ["tableId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("tableId"),
        Index("status"),
        Index("orderNumber")
    ]
)
data class OrderEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val orderNumber: String,       // Auto-generated: ORD-20240209-001
    val tableId: String,
    val cashierId: String,
    val cashierName: String,
    val customerName: String = "",
    val customerPhone: String = "",
    val status: String = OrderStatus.OPEN,
    val subtotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val discountPercent: Double = 0.0,
    val discountNote: String = "",
    val serviceCharge: Double = 0.0,     // Service charge amount
    val serviceChargePercent: Double = 0.0,
    val taxAmount: Double = 0.0,
    val taxPercent: Double = 0.0,        // Applied tax percent
    val grandTotal: Double = 0.0,
    val totalQuantity: Int = 0,
    val note: String = "",               // Order-level note
    val isHeld: Boolean = false,
    val openedAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = SyncStatus.PENDING,
    val syncId: String? = null
)

object OrderStatus {
    const val OPEN = "OPEN"
    const val HELD = "HELD"
    const val PAID = "PAID"
    const val CLOSED = "CLOSED"
    const val VOID = "VOID"
    const val MERGED = "MERGED"
}
