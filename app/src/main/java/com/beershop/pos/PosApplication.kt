package com.beershop.pos

import android.app.Application
import com.beershop.pos.data.DataInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PosApplication : Application() {
    @Inject lateinit var dataInitializer: DataInitializer

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dataInitializer.initialize()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
