package com.beershop.pos.data.local.dao

import androidx.room.*
import com.beershop.pos.data.local.entity.TableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {
    @Query("SELECT * FROM tables WHERE isActive = 1 ORDER BY tableNumber")
    fun getAllTables(): Flow<List<TableEntity>>

    @Query("SELECT * FROM tables WHERE id = :tableId")
    suspend fun getTableById(tableId: String): TableEntity?

    @Query("SELECT * FROM tables WHERE tableNumber = :tableNumber AND isActive = 1")
    suspend fun getTableByNumber(tableNumber: String): TableEntity?

    @Query("SELECT * FROM tables WHERE status = :status AND isActive = 1")
    fun getTablesByStatus(status: String): Flow<List<TableEntity>>

    @Query("SELECT * FROM tables WHERE zone = :zone AND isActive = 1 ORDER BY tableNumber")
    fun getTablesByZone(zone: String): Flow<List<TableEntity>>

    @Query("SELECT COUNT(*) FROM tables WHERE status = :status AND isActive = 1")
    suspend fun getTableCountByStatus(status: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTable(table: TableEntity)

    @Update
    suspend fun updateTable(table: TableEntity)

    @Query("UPDATE tables SET status = :status, currentOrderId = null, updatedAt = :timestamp WHERE id = :tableId")
    suspend fun updateTableStatus(tableId: String, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE tables SET status = :status, currentOrderId = :orderId, updatedAt = :timestamp WHERE id = :tableId")
    suspend fun assignOrderToTable(tableId: String, orderId: String?, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE tables SET isActive = 0 WHERE id = :tableId")
    suspend fun deactivateTable(tableId: String)

    @Query("DELETE FROM tables WHERE id = :tableId")
    suspend fun deleteTable(tableId: String)

    @Query("SELECT * FROM tables WHERE syncStatus = 'PENDING' AND isActive = 1")
    suspend fun getPendingSyncTables(): List<TableEntity>

    @Query("SELECT DISTINCT zone FROM tables WHERE isActive = 1 ORDER BY zone")
    fun getZones(): Flow<List<String>>
}
