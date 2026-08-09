package com.beershop.pos

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.beershop.pos.data.DataInitializer
import com.beershop.pos.sync.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BeerShopPOSApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var dataInitializer: DataInitializer

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        instance = this
        SyncWorker.schedule(this)

        // Initialize default data (runs only on first launch)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dataInitializer.initialize()
            } catch (e: Exception) {
                android.util.Log.e("BeerShopPOS", "Error initializing data", e)
            }
        }
    }

    companion object {
        lateinit var instance: BeerShopPOSApp
            private set
    }
}
