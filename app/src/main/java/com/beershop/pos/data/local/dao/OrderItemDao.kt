package com.beershop.pos.data.local.dao

import androidx.room.*
import com.beershop.pos.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    @Query("SELECT * FROM order_items WHERE orderId = :orderId AND isVoid = 0 ORDER BY sortOrder, createdAt")
    fun getItemsForOrder(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId AND isVoid = 0 ORDER BY sortOrder, createdAt")
    suspend fun getItemsForOrderSync(orderId: String): List<OrderItemEntity>

    @Query("SELECT * FROM order_items WHERE id = :itemId")
    suspend fun getItemById(itemId: String): OrderItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: OrderItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<OrderItemEntity>)

    @Update
    suspend fun updateItem(item: OrderItemEntity)

    @Query("UPDATE order_items SET quantity = :quantity, totalPrice = :totalPrice WHERE id = :itemId")
    suspend fun updateItemQuantity(itemId: String, quantity: Int, totalPrice: Double)

    @Query("UPDATE order_items SET note = :note WHERE id = :itemId")
    suspend fun updateItemNote(itemId: String, note: String)

    @Query("UPDATE order_items SET isVoid = 1, voidReason = :reason WHERE id = :itemId")
    suspend fun voidItem(itemId: String, reason: String = "")

    @Query("DELETE FROM order_items WHERE id = :itemId AND isVoid = 1")
    suspend fun deleteItem(itemId: String)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteAllItemsForOrder(orderId: String)

    @Query("SELECT COUNT(*) FROM order_items WHERE orderId = :orderId AND isVoid = 0")
    suspend fun getItemCount(orderId: String): Int

    @Query("""
        SELECT oi.productId, p.name, p.category, SUM(oi.quantity) as totalQty, SUM(oi.totalPrice) as totalSales
        FROM order_items oi
        INNER JOIN products p ON oi.productId = p.id
        INNER JOIN orders o ON oi.orderId = o.id
        WHERE date(o.openedAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch')
        AND o.status != 'VOID' AND oi.isVoid = 0
        GROUP BY oi.productId
        ORDER BY totalSales DESC
    """)
    suspend fun getSalesByProductDaily(date: Long = System.currentTimeMillis()): List<ProductSalesResult>

    data class ProductSalesResult(
        val productId: String,
        val name: String,
        val category: String,
        val totalQty: Int,
        val totalSales: Double
    )
}
