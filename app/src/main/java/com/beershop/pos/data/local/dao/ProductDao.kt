package com.beershop.pos.data.local.dao

import androidx.room.*
import com.beershop.pos.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY category, name")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY category, name")
    suspend fun getAllProductsList(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE barcode = :barcode AND isActive = 1")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    @Query("SELECT * FROM products WHERE category = :category AND isActive = 1 ORDER BY name")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE (name LIKE '%' || :query || '%' OR nameMyanmar LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%') AND isActive = 1 ORDER BY name")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE (name LIKE '%' || :query || '%' OR nameMyanmar LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%') AND category = :category AND isActive = 1 ORDER BY name")
    fun searchProductsInCategory(query: String, category: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQuantity = stockQuantity - :quantity WHERE id = :productId")
    suspend fun decreaseStock(productId: String, quantity: Double)

    @Query("UPDATE products SET stockQuantity = stockQuantity + :quantity WHERE id = :productId")
    suspend fun increaseStock(productId: String, quantity: Double)

    @Query("UPDATE products SET isActive = 0 WHERE id = :productId")
    suspend fun deactivateProduct(productId: String)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("SELECT DISTINCT category FROM products WHERE isActive = 1 ORDER BY category")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT * FROM products WHERE syncStatus = 'PENDING' AND isActive = 1")
    suspend fun getPendingSyncProducts(): List<ProductEntity>
}
