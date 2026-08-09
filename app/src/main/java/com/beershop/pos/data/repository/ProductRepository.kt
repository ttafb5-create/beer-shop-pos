package com.beershop.pos.data.repository

import com.beershop.pos.data.local.dao.ProductDao
import com.beershop.pos.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    suspend fun getAllProductsList() = productDao.getAllProductsList()
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = productDao.getProductsByCategory(category)
    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProducts(query)
    fun searchProductsInCategory(query: String, category: String): Flow<List<ProductEntity>> = productDao.searchProductsInCategory(query, category)
    fun getCategories(): Flow<List<String>> = productDao.getCategories()
    suspend fun getProductById(productId: String) = productDao.getProductById(productId)
    suspend fun getProductByBarcode(barcode: String) = productDao.getProductByBarcode(barcode)

    suspend fun createProduct(
        name: String,
        nameMyanmar: String,
        category: String,
        sellingPrice: Double,
        costPrice: Double,
        stockQuantity: Double,
        unit: String,
        barcode: String?,
        imagePath: String?,
        taxRate: Double
    ): ProductEntity {
        val product = ProductEntity(
            name = name,
            nameMyanmar = nameMyanmar,
            category = category,
            sellingPrice = sellingPrice,
            costPrice = costPrice,
            stockQuantity = stockQuantity,
            unit = unit,
            barcode = barcode,
            imagePath = imagePath,
            taxRate = taxRate
        )
        productDao.insertProduct(product)
        return product
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deactivateProduct(productId: String) = productDao.deactivateProduct(productId)
    suspend fun deleteProduct(productId: String) = productDao.deleteProduct(productId)

    suspend fun decreaseStock(productId: String, quantity: Double) = productDao.decreaseStock(productId, quantity)
    suspend fun increaseStock(productId: String, quantity: Double) = productDao.increaseStock(productId, quantity)
}
