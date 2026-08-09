package com.beershop.pos.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.beershop.pos.data.local.AppDatabase
import com.beershop.pos.data.local.entity.SyncStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: AppDatabase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!isNetworkAvailable()) {
            return Result.retry()
        }

        return try {
            syncUsers()
            syncProducts()
            syncTables()
            syncOrders()
            syncPayments()
            syncDailySummaries()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private suspend fun syncUsers() {
        val pending = database.userDao().getPendingSyncUsers()
        for (user in pending) {
            try {
                // Upload to cloud - placeholder for Firebase/Firestore
                // val result = cloudService.syncUser(user)
                // if (result.isSuccess) {
                //     database.userDao().updateUser(user.copy(syncStatus = SyncStatus.SYNCED))
                // }
            } catch (e: Exception) {
                database.userDao().updateUser(user.copy(syncStatus = SyncStatus.FAILED))
            }
        }
    }

    private suspend fun syncProducts() {
        val pending = database.productDao().getPendingSyncProducts()
        for (product in pending) {
            try {
                // Upload to cloud
            } catch (e: Exception) {
                database.productDao().updateProduct(product.copy(syncStatus = SyncStatus.FAILED))
            }
        }
    }

    private suspend fun syncTables() {
        val pending = database.tableDao().getPendingSyncTables()
        for (table in pending) {
            try {
                // Upload to cloud
            } catch (e: Exception) {
                database.tableDao().updateTable(table.copy(syncStatus = SyncStatus.FAILED))
            }
        }
    }

    private suspend fun syncOrders() {
        val pending = database.orderDao().getPendingSyncOrders()
        for (order in pending) {
            try {
                // Upload to cloud
            } catch (e: Exception) {
                database.orderDao().updateOrder(order.copy(syncStatus = SyncStatus.FAILED))
            }
        }
    }

    private suspend fun syncPayments() {
        val pending = database.paymentDao().getPendingSyncPayments()
        for (payment in pending) {
            try {
                // Upload to cloud - with transactionId dedup
            } catch (e: Exception) {
                // Leave as pending for retry
            }
        }
    }

    private suspend fun syncDailySummaries() {
        // Sync daily summaries
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        const val WORK_NAME = "pos_sync_work"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    1, TimeUnit.MINUTES
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }

        fun triggerManualSync(context: Context) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueue(request)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(WORK_NAME)
        }
    }
}
