package com.beershop.pos.data.repository

import com.beershop.pos.data.local.dao.PaymentDao
import com.beershop.pos.data.local.entity.PaymentEntity
import com.beershop.pos.data.local.entity.PaymentMethod
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val paymentDao: PaymentDao
) {
    suspend fun processPayment(
        orderId: String,
        method: String,
        amount: Double,
        referenceNumber: String = "",
        note: String = "",
        cashierId: String,
        cashierName: String
    ): PaymentEntity {
        val payment = PaymentEntity(
            orderId = orderId,
            transactionId = "TXN-${UUID.randomUUID().toString().take(8).uppercase()}",
            method = method,
            amount = amount,
            referenceNumber = referenceNumber,
            note = note,
            cashierId = cashierId,
            cashierName = cashierName
        )
        paymentDao.insertPayment(payment)
        return payment
    }

    suspend fun processMixedPayment(
        orderId: String,
        payments: List<Pair<String, Double>>,  // List of (method, amount)
        cashierId: String,
        cashierName: String
    ): List<PaymentEntity> {
        return payments.map { (method, amount) ->
            PaymentEntity(
                orderId = orderId,
                transactionId = "TXN-${UUID.randomUUID().toString().take(8).uppercase()}",
                method = method,
                amount = amount,
                cashierId = cashierId,
                cashierName = cashierName
            )
        }.also { paymentDao.insertPayments(it) }
    }

    fun getPaymentsForOrder(orderId: String): Flow<List<PaymentEntity>> =
        paymentDao.getPaymentsForOrder(orderId)

    suspend fun getPaymentsForOrderSync(orderId: String) =
        paymentDao.getPaymentsForOrderSync(orderId)

    fun getPaymentsByDate(date: Long = System.currentTimeMillis()): Flow<List<PaymentEntity>> =
        paymentDao.getPaymentsByDate(date)

    fun getPaymentsBetweenDates(startDate: Long, endDate: Long): Flow<List<PaymentEntity>> =
        paymentDao.getPaymentsBetweenDates(startDate, endDate)

    suspend fun getDailyTotal(date: Long = System.currentTimeMillis()): Double =
        paymentDao.getDailyTotal(date) ?: 0.0

    suspend fun getDailyTotalByMethod(method: String, date: Long = System.currentTimeMillis()): Double =
        paymentDao.getDailyTotalByMethod(method, date) ?: 0.0

    suspend fun getDailySalesBreakdown(date: Long = System.currentTimeMillis()) =
        paymentDao.getDailySalesBreakdown(date)

    suspend fun getSalesBreakdownBetweenDates(startDate: Long, endDate: Long) =
        paymentDao.getSalesBreakdownBetweenDates(startDate, endDate)

    suspend fun refundPayment(paymentId: String) {
        val payment = paymentDao.getPaymentByTransactionId(paymentId) ?: return
        paymentDao.insertPayment(payment.copy(
            id = UUID.randomUUID().toString(),
            isRefund = true,
            refundAmount = payment.amount,
            amount = -payment.amount,
            transactionId = "REF-${payment.transactionId}"
        ))
    }
}
