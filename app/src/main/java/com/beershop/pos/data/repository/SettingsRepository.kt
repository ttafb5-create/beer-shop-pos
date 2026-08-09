package com.beershop.pos.data.repository

import com.beershop.pos.data.local.dao.SettingsDao
import com.beershop.pos.data.local.entity.SettingsEntity
import com.beershop.pos.data.local.entity.SettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) {
    fun getAllSettings(): Flow<List<SettingsEntity>> = settingsDao.getAllSettings()

    suspend fun getSetting(key: String, default: String = ""): String {
        return settingsDao.getSetting(key)?.value ?: default
    }

    suspend fun getSettingBoolean(key: String, default: Boolean = false): Boolean {
        return settingsDao.getSetting(key)?.value?.toBooleanStrictOrNull() ?: default
    }

    suspend fun getSettingDouble(key: String, default: Double = 0.0): Double {
        return settingsDao.getSetting(key)?.value?.toDoubleOrNull() ?: default
    }

    suspend fun getSettingInt(key: String, default: Int = 0): Int {
        return settingsDao.getSetting(key)?.value?.toIntOrNull() ?: default
    }

    suspend fun setSetting(key: String, value: String) {
        settingsDao.setSetting(SettingsEntity(key = key, value = value))
    }

    fun getShopName(): Flow<String> = settingsDao.getAllSettings().map { settings ->
        settings.find { it.key == SettingsKeys.SHOP_NAME }?.value ?: "Beer Shop"
    }

    fun isDarkMode(): Flow<Boolean> = settingsDao.getAllSettings().map { settings ->
        settings.find { it.key == SettingsKeys.DARK_MODE }?.value?.toBooleanStrictOrNull() ?: false
    }

    fun getLanguage(): Flow<String> = settingsDao.getAllSettings().map { settings ->
        settings.find { it.key == SettingsKeys.LANGUAGE }?.value ?: "my"
    }

    suspend fun getCurrency(): String = getSetting(SettingsKeys.CURRENCY, "Ks")

    suspend fun getTaxRate(): Double = getSettingDouble(SettingsKeys.DEFAULT_TAX_RATE, 5.0)
    suspend fun getServiceChargeRate(): Double = getSettingDouble(SettingsKeys.DEFAULT_SERVICE_CHARGE, 10.0)
    suspend fun getPrinterPaperWidth(): Int = getSettingInt(SettingsKeys.PRINTER_PAPER_WIDTH, 58)
}
