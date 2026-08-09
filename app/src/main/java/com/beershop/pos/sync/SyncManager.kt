package com.beershop.pos.sync

import android.content.Context
import com.beershop.pos.data.local.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

data class SyncStatusState(
    val isOnline: Boolean = false,
    val syncInProgress: Boolean = false,
    val pendingCount: Int = 0,
    val syncedCount: Int = 0,
    val failedCount: Int = 0,
    val lastSyncTime: Long? = null,
    val error: String? = null
)

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) {
    fun getSyncStatus(): Flow<SyncStatusState> = flow {
        // Emit initial state
        val state = SyncStatusState()
        // Will be updated by observing network and work manager
        emit(state)
    }

    suspend fun getPendingCounts(): Int {
        val users = database.userDao().getPendingSyncUsers().size
        val products = database.productDao().getPendingSyncProducts().size
        val tables = database.tableDao().getPendingSyncTables().size
        val orders = database.orderDao().getPendingSyncOrders().size
        val payments = database.paymentDao().getPendingSyncPayments().size
        return users + products + tables + orders + payments
    }

    fun triggerSync() {
        SyncWorker.triggerManualSync(context)
    }
}
