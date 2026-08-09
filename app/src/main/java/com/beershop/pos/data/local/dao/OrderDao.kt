package com.beershop.pos.data.local.dao

import androidx.room.*
import com.beershop.pos.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE status IN ('OPEN', 'HELD') ORDER BY openedAt DESC")
    fun getOpenOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE tableId = :tableId AND status IN ('OPEN', 'HELD') LIMIT 1")
    suspend fun getOpenOrderForTable(tableId: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE tableId = :tableId AND status != 'VOID' ORDER BY openedAt DESC")
    fun getOrdersForTable(tableId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber")
    suspend fun getOrderByNumber(orderNumber: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY updatedAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE date(openedAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch') AND status != 'VOID' ORDER BY openedAt DESC")
    fun getOrdersByDate(date: Long = System.currentTimeMillis()): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE date(openedAt/1000, 'unixepoch') BETWEEN date(:startDate/1000, 'unixepoch') AND date(:endDate/1000, 'unixepoch') AND status != 'VOID' ORDER BY openedAt DESC")
    fun getOrdersBetweenDates(startDate: Long, endDate: Long): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, closedAt = :closedAt, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, closedAt: Long? = null, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET subtotal = :subtotal, discountAmount = :discount, taxAmount = :tax, serviceCharge = :sc, grandTotal = :total, totalQuantity = :qty, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updateOrderTotals(orderId: String, subtotal: Double, discount: Double, tax: Double, sc: Double, total: Double, qty: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET tableId = :newTableId, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun transferOrder(orderId: String, newTableId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET status = 'MERGED', updatedAt = :timestamp WHERE id = :orderId")
    suspend fun markAsMerged(orderId: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM orders WHERE id = :orderId AND status = 'VOID'")
    suspend fun deleteVoidedOrder(orderId: String)

    @Query("SELECT COUNT(*) FROM orders WHERE status = :status AND cashierId = :cashierId")
    suspend fun getOrderCountByStatus(cashierId: String, status: String): Int

    @Query("SELECT * FROM orders WHERE syncStatus = 'PENDING'")
    suspend fun getPendingSyncOrders(): List<OrderEntity>

    @Query("SELECT COUNT(*) FROM orders WHERE date(openedAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch') AND status != 'VOID'")
    suspend fun getDailyOrderCount(date: Long = System.currentTimeMillis()): Int
}
