package com.beershop.pos.data.local.dao

import androidx.room.*
import com.beershop.pos.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE orderId = :orderId ORDER BY createdAt")
    fun getPaymentsForOrder(orderId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE orderId = :orderId ORDER BY createdAt")
    suspend fun getPaymentsForOrderSync(orderId: String): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE transactionId = :transactionId LIMIT 1")
    suspend fun getPaymentByTransactionId(transactionId: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE date(createdAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch') AND isRefund = 0 ORDER BY createdAt DESC")
    fun getPaymentsByDate(date: Long = System.currentTimeMillis()): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE date(createdAt/1000, 'unixepoch') BETWEEN date(:startDate/1000, 'unixepoch') AND date(:endDate/1000, 'unixepoch') AND isRefund = 0 ORDER BY createdAt DESC")
    fun getPaymentsBetweenDates(startDate: Long, endDate: Long): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("SELECT SUM(amount) FROM payments WHERE date(createdAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch') AND isRefund = 0")
    suspend fun getDailyTotal(date: Long = System.currentTimeMillis()): Double?

    @Query("SELECT SUM(amount) FROM payments WHERE method = :method AND date(createdAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch') AND isRefund = 0")
    suspend fun getDailyTotalByMethod(method: String, date: Long = System.currentTimeMillis()): Double?

    @Query("SELECT method, SUM(amount) as total FROM payments WHERE date(createdAt/1000, 'unixepoch') = date(:date/1000, 'unixepoch') AND isRefund = 0 GROUP BY method")
    suspend fun getDailySalesBreakdown(date: Long = System.currentTimeMillis()): List<MethodBreakdown>

    @Query("""
        SELECT method, SUM(amount) as total FROM payments
        WHERE date(createdAt/1000, 'unixepoch') BETWEEN date(:startDate/1000, 'unixepoch') AND date(:endDate/1000, 'unixepoch')
        AND isRefund = 0 GROUP BY method
    """)
    suspend fun getSalesBreakdownBetweenDates(startDate: Long, endDate: Long): List<MethodBreakdown>

    data class MethodBreakdown(
        val method: String,
        val total: Double
    )

    @Query("SELECT * FROM payments WHERE syncStatus = 'PENDING'")
    suspend fun getPendingSyncPayments(): List<PaymentEntity>
}
