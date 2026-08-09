package com.beershop.pos.data.local.entity

import androidx.room.*
import java.util.UUID

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("orderId"),
        Index("productId")
    ]
)
data class OrderItemEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val orderId: String,
    val productId: String,
    val productName: String,
    val productNameMyanmar: String = "",
    val category: String,
    val unitPrice: Double,
    val quantity: Int = 1,
    val discountAmount: Double = 0.0,
    val discountPercent: Double = 0.0,
    val taxAmount: Double = 0.0,
    val taxPercent: Double = 0.0,
    val totalPrice: Double,          // (unitPrice * quantity) - discount + tax
    val note: String = "",
    val isVoid: Boolean = false,
    val voidReason: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)
