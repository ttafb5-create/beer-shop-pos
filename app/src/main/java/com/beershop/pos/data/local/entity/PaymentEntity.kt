package com.beershop.pos.data.local.entity

import androidx.room.*
import java.util.UUID

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId"), Index("transactionId")]
)
data class PaymentEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val orderId: String,
    val transactionId: String,      // Unique transaction ID for sync
    val method: String,             // CASH, KBZPAY, WAVE_MONEY, AYA_PAY, CB_PAY, BANK_TRANSFER, OTHER
    val amount: Double,
    val referenceNumber: String = "",
    val note: String = "",
    val isRefund: Boolean = false,
    val refundAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val cashierId: String,
    val cashierName: String,
    val syncStatus: String = SyncStatus.PENDING,
    val syncId: String? = null
)

object PaymentMethod {
    const val CASH = "CASH"
    const val KBZPAY = "KBZPAY"
    const val WAVE_MONEY = "WAVE_MONEY"
    const val AYA_PAY = "AYA_PAY"
    const val CB_PAY = "CB_PAY"
    const val BANK_TRANSFER = "BANK_TRANSFER"
    const val OTHER = "OTHER"

    val ALL = listOf(CASH, KBZPAY, WAVE_MONEY, AYA_PAY, CB_PAY, BANK_TRANSFER, OTHER)

    fun displayName(method: String): String {
        return when (method) {
            CASH -> "Cash / ငွေသား"
            KBZPAY -> "KBZPay"
            WAVE_MONEY -> "Wave Money"
            AYA_PAY -> "AYA Pay"
            CB_PAY -> "CB Pay"
            BANK_TRANSFER -> "Bank Transfer / ဘဏ်လွှဲ"
            OTHER -> "Other / အခြား"
            else -> method
        }
    }

    fun isWallet(method: String): Boolean {
        return method in listOf(KBZPAY, WAVE_MONEY, AYA_PAY, CB_PAY)
    }
}
