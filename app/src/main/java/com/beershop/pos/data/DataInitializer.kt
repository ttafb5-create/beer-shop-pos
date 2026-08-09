package com.beershop.pos.data

import com.beershop.pos.data.local.dao.*
import com.beershop.pos.data.local.entity.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val tableDao: TableDao,
    private val settingsDao: SettingsDao
) {
    suspend fun initialize() {
        createDefaultUsers()
        createDefaultSettings()
        createDefaultProducts()
        createDefaultTables()
    }

    private suspend fun createDefaultUsers() {
        if (userDao.getUserByUsername("admin") == null) {
            userDao.insertUser(
                UserEntity(
                    username = "admin",
                    passwordHash = hashPasswordSHA256("admin123"),
                    displayName = "Admin Owner",
                    role = UserRole.OWNER,
                    syncStatus = SyncStatus.NOT_SYNCED
                )
            )
        }
        if (userDao.getUserByUsername("manager") == null) {
            userDao.insertUser(
                UserEntity(
                    username = "manager",
                    passwordHash = hashPasswordSHA256("manager123"),
                    displayName = "Manager",
                    role = UserRole.MANAGER,
                    syncStatus = SyncStatus.NOT_SYNCED
                )
            )
        }
        if (userDao.getUserByUsername("cashier") == null) {
            userDao.insertUser(
                UserEntity(
                    username = "cashier",
                    passwordHash = hashPasswordSHA256("cashier123"),
                    displayName = "Cashier",
                    role = UserRole.CASHIER,
                    syncStatus = SyncStatus.NOT_SYNCED
                )
            )
        }
    }

    private suspend fun createDefaultSettings() {
        val defaults = mapOf(
            SettingsKeys.SHOP_NAME to "Beer Shop",
            SettingsKeys.SHOP_ADDRESS to "Yangon, Myanmar",
            SettingsKeys.SHOP_PHONE to "09-123456789",
            SettingsKeys.CURRENCY to "Ks",
            SettingsKeys.DEFAULT_TAX_RATE to "5",
            SettingsKeys.DEFAULT_SERVICE_CHARGE to "10",
            SettingsKeys.PRINTER_PAPER_WIDTH to "58",
            SettingsKeys.LANGUAGE to "my",
            SettingsKeys.DARK_MODE to "false",
            SettingsKeys.CLOUD_SYNC_ENABLED to "true",
            SettingsKeys.RECEIPT_FOOTER to "Thank You - ကျေးဇူးတင်ပါတယ်"
        )

        for ((key, value) in defaults) {
            if (settingsDao.getSetting(key) == null) {
                settingsDao.setSetting(SettingsEntity(key = key, value = value))
            }
        }
    }

    private suspend fun createDefaultProducts() {
        if (productDao.getAllProductsList().isEmpty()) {
            val products = listOf(
                ProductEntity(name = "Myanmar Beer", nameMyanmar = "မြန်မာဘီယာ", category = ProductCategory.BEER, sellingPrice = 2500.0, costPrice = 1800.0, stockQuantity = 200.0, unit = "ဘူး", taxRate = 5.0),
                ProductEntity(name = "Tiger Beer", nameMyanmar = "တိုင်းဂါးဘီယာ", category = ProductCategory.BEER, sellingPrice = 2800.0, costPrice = 2100.0, stockQuantity = 150.0, unit = "ဘူး", taxRate = 5.0),
                ProductEntity(name = "ABC Stout", nameMyanmar = "ABC Stout", category = ProductCategory.BEER, sellingPrice = 3000.0, costPrice = 2300.0, stockQuantity = 100.0, unit = "ဘူး", taxRate = 5.0),
                ProductEntity(name = "Heineken", nameMyanmar = "ဟိုင်နီကင်", category = ProductCategory.BEER, sellingPrice = 3200.0, costPrice = 2500.0, stockQuantity = 120.0, unit = "ဘူး", taxRate = 5.0),
                ProductEntity(name = "Dagon Beer", nameMyanmar = "ဒဂုံဘီယာ", category = ProductCategory.BEER, sellingPrice = 2000.0, costPrice = 1400.0, stockQuantity = 250.0, unit = "ဘူး", taxRate = 5.0),
                ProductEntity(name = "Grand Royal Whisky", nameMyanmar = "Grand Royal ဝီစကီ", category = ProductCategory.WHISKY, sellingPrice = 8500.0, costPrice = 6500.0, stockQuantity = 80.0, unit = "ဘူး", taxRate = 10.0),
                ProductEntity(name = "Johnnie Walker Red", nameMyanmar = "Johnnie Walker Red", category = ProductCategory.WHISKY, sellingPrice = 25000.0, costPrice = 20000.0, stockQuantity = 30.0, unit = "ဘူး", taxRate = 10.0),
                ProductEntity(name = "Mandalay Rum", nameMyanmar = "မန္တလေးရမ်", category = ProductCategory.ALCOHOL, sellingPrice = 5000.0, costPrice = 3500.0, stockQuantity = 50.0, unit = "ဘူး", taxRate = 10.0),
                ProductEntity(name = "Coca Cola", nameMyanmar = "ကိုကာကိုလာ", category = ProductCategory.SOFT_DRINK, sellingPrice = 1000.0, costPrice = 700.0, stockQuantity = 300.0, unit = "ဘူး", taxRate = 5.0),
                ProductEntity(name = "Sprite", nameMyanmar = "စပရိုက်", category = ProductCategory.SOFT_DRINK, sellingPrice = 1000.0, costPrice = 700.0, stockQuantity = 250.0, unit = "ဘူး", taxRate = 5.0),
                ProductEntity(name = "Water", nameMyanmar = "ရေ", category = ProductCategory.SOFT_DRINK, sellingPrice = 500.0, costPrice = 200.0, stockQuantity = 500.0, unit = "ဘူး", taxRate = 0.0),
                ProductEntity(name = "Fried Chicken", nameMyanmar = "ကြက်ကြော်", category = ProductCategory.FOOD, sellingPrice = 5000.0, costPrice = 2500.0, stockQuantity = 40.0, unit = "ပွဲ", taxRate = 5.0),
                ProductEntity(name = "Fried Rice", nameMyanmar = "ထမင်းကြော်", category = ProductCategory.FOOD, sellingPrice = 3500.0, costPrice = 1500.0, stockQuantity = 50.0, unit = "ပွဲ", taxRate = 5.0),
                ProductEntity(name = "Peanuts", nameMyanmar = "မြေပဲ", category = ProductCategory.SNACK, sellingPrice = 1500.0, costPrice = 800.0, stockQuantity = 100.0, unit = "ပွဲ", taxRate = 5.0),
                ProductEntity(name = "Sunflower Seeds", nameMyanmar = "နေကြာစေ့", category = ProductCategory.SNACK, sellingPrice = 1000.0, costPrice = 500.0, stockQuantity = 150.0, unit = "ထုပ်", taxRate = 5.0)
            )
            productDao.insertProducts(products.map { it.copy(syncStatus = SyncStatus.NOT_SYNCED) })
        }
    }

    private suspend fun createDefaultTables() {
        if (tableDao.getTableById("t1") == null) {
            val tables = listOf(
                TableEntity(id = "t1", tableNumber = "1", tableName = "Table 1", capacity = 4, zone = "Main"),
                TableEntity(id = "t2", tableNumber = "2", tableName = "Table 2", capacity = 4, zone = "Main"),
                TableEntity(id = "t3", tableNumber = "3", tableName = "Table 3", capacity = 4, zone = "Main"),
                TableEntity(id = "t4", tableNumber = "4", tableName = "Table 4", capacity = 6, zone = "Main"),
                TableEntity(id = "t5", tableNumber = "5", tableName = "Table 5", capacity = 6, zone = "Main"),
                TableEntity(id = "t6", tableNumber = "6", tableName = "Table 6", capacity = 4, zone = "Main"),
                TableEntity(id = "t7", tableNumber = "7", tableName = "Table 7", capacity = 8, zone = "Main"),
                TableEntity(id = "t8", tableNumber = "8", tableName = "Table 8", capacity = 4, zone = "Main"),
                TableEntity(id = "vip1", tableNumber = "VIP-1", tableName = "VIP Room 1", capacity = 10, zone = "VIP"),
                TableEntity(id = "vip2", tableNumber = "VIP-2", tableName = "VIP Room 2", capacity = 8, zone = "VIP"),
                TableEntity(id = "out1", tableNumber = "O-1", tableName = "Outdoor 1", capacity = 4, zone = "Outdoor"),
                TableEntity(id = "out2", tableNumber = "O-2", tableName = "Outdoor 2", capacity = 4, zone = "Outdoor")
            )
            for (table in tables) {
                tableDao.insertTable(table.copy(syncStatus = SyncStatus.NOT_SYNCED))
            }
        }
    }

    private fun hashPasswordSHA256(password: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
