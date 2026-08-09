package com.beershop.pos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "daily_sales_summary")
data class DailySalesSummaryEntity(
    @PrimaryKey
    val date: String,              // yyyy-MM-dd
    val totalOrders: Int = 0,
    val totalSales: Double = 0.0,
    val totalCost: Double = 0.0,
    val totalProfit: Double = 0.0,
    val totalTax: Double = 0.0,
    val totalServiceCharge: Double = 0.0,
    val totalDiscount: Double = 0.0,
    val cashSales: Double = 0.0,
    val walletSales: Double = 0.0,
    val openTablesCount: Int = 0,
    val closedTablesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = SyncStatus.PENDING,
    val syncId: String? = null
)
