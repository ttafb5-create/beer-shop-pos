package com.beershop.pos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val key: String,               // e.g., "shop_name", "currency", "tax_rate"
    val value: String,
    val updatedAt: Long = System.currentTimeMillis()
)

object SettingsKeys {
    const val SHOP_NAME = "shop_name"
    const val SHOP_ADDRESS = "shop_address"
    const val SHOP_PHONE = "shop_phone"
    const val CURRENCY = "currency"
    const val DEFAULT_TAX_RATE = "default_tax_rate"
    const val DEFAULT_SERVICE_CHARGE = "default_service_charge"
    const val RECEIPT_FOOTER = "receipt_footer"
    const val PRINTER_PAPER_WIDTH = "printer_paper_width" // 58 or 80
    const val LANGUAGE = "language"          // en, my
    const val DARK_MODE = "dark_mode"
    const val CLOUD_SYNC_ENABLED = "cloud_sync_enabled"
    const val CLOUD_SYNC_URL = "cloud_sync_url"
    const val RECEIPT_HEADER_LOGO = "receipt_header_logo"
}
