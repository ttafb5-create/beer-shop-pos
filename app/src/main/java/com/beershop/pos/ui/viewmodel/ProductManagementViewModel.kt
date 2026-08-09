package com.beershop.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beershop.pos.data.local.dao.ProductDao
import com.beershop.pos.data.local.entity.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductManagementState(
    val products: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProductManagementViewModel @Inject constructor(
    private val productDao: ProductDao
) : ViewModel() {

    private val _state = MutableStateFlow(ProductManagementState())
    val state: StateFlow<ProductManagementState> = _state.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            productDao.getAllProducts().collect { products ->
                _state.update { it.copy(products = products, isLoading = false) }
            }
        }
    }

    fun addProduct(name: String, price: Double, category: String, unit: String = "Bottle", stock: Double = 0.0) {
        viewModelScope.launch {
            val product = ProductEntity(
                name = name,
                price = price,
                category = category,
                unit = unit,
                stock = stock
            )
            productDao.insertProduct(product)
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            productDao.updateProduct(product)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            productDao.deactivateProduct(productId)
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadProducts()
            } else {
                productDao.searchProducts("%$query%").collect { products ->
                    _state.update { it.copy(products = products) }
                }
            }
        }
    }
}