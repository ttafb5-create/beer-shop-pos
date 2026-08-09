package com.beershop.pos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.beershop.pos.data.local.dao.*
import com.beershop.pos.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        TableEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        PaymentEntity::class,
        SettingsEntity::class,
        DailySalesSummaryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tableDao(): TableDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun paymentDao(): PaymentDao
    abstract fun reportDao(): ReportDao
    abstract fun settingsDao(): SettingsDao
}
