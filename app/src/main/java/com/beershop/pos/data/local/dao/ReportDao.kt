package com.beershop.pos.data.local.dao

import androidx.room.*
import com.beershop.pos.data.local.entity.DailySalesSummaryEntity
import com.beershop.pos.data.local.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM daily_sales_summary WHERE date = :date")
    suspend fun getDailySummary(date: String): DailySalesSummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailySummary(summary: DailySalesSummaryEntity)

    @Query("SELECT * FROM daily_sales_summary WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    suspend fun getSummaryBetweenDates(startDate: String, endDate: String): List<DailySalesSummaryEntity>
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE `key` = :key")
    suspend fun getSetting(key: String): SettingsEntity?

    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<SettingsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingsEntity)

    @Query("DELETE FROM settings WHERE `key` = :key")
    suspend fun deleteSetting(key: String)
}