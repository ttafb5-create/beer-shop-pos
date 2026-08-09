package com.beershop.pos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,              // English name
    val nameMyanmar: String = "",  // Myanmar name
    val category: String,          // BEER, WHISKY, ALCOHOL, SOFT_DRINK, FOOD, SNACK, OTHER
    val sellingPrice: Double,
    val costPrice: Double = 0.0,
    val stockQuantity: Double = 0.0,
    val unit: String = "ဘူး",      // bottle, glass, plate, etc.
    val barcode: String? = null,
    val imagePath: String? = null,
    val isActive: Boolean = true,
    val taxRate: Double = 0.0,     // Tax rate percentage
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = SyncStatus.PENDING,
    val syncId: String? = null
)

object ProductCategory {
    const val BEER = "BEER"
    const val WHISKY = "WHISKY"
    const val ALCOHOL = "ALCOHOL"
    const val SOFT_DRINK = "SOFT_DRINK"
    const val FOOD = "FOOD"
    const val SNACK = "SNACK"
    const val OTHER = "OTHER"

    val ALL = listOf(BEER, WHISKY, ALCOHOL, SOFT_DRINK, FOOD, SNACK, OTHER)

    fun displayName(category: String): String {
        return when (category) {
            BEER -> "ဘီယာ"
            WHISKY -> "ဝီစကီ"
            ALCOHOL -> "အရက်"
            SOFT_DRINK -> "ဖျော်ရည်"
            FOOD -> "အစားအသောက်"
            SNACK -> "သရေစာ"
            OTHER -> "အခြား"
            else -> category
        }
    }

    fun emoji(category: String): String {
        return when (category) {
            BEER -> "🍺"
            WHISKY -> "🥃"
            ALCOHOL -> "🍾"
            SOFT_DRINK -> "🧃"
            FOOD -> "🍽️"
            SNACK -> "🍿"
            OTHER -> "📦"
            else -> "📦"
        }
    }
}
