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

    fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            productDao.getAllProducts().collect { products ->
                _state.update { it.copy(products = products, isLoading = false) }
            }
        }
    }

    fun addProduct(
        name: String,
        nameMyanmar: String = "",
        category: String = "BEER",
        sellingPrice: Double,
        costPrice: Double = 0.0,
        stock: Double = 0.0,
        unit: String = "\u1018\u1030\u1038",
        barcode: String? = null,
        taxRate: Double = 0.0
    ) {
        viewModelScope.launch {
            val product = ProductEntity(
                name = name,
                nameMyanmar = nameMyanmar,
                category = category,
                sellingPrice = sellingPrice,
                costPrice = costPrice,
                stockQuantity = stock,
                unit = unit,
                barcode = barcode,
                taxRate = taxRate
            )
            productDao.insertProduct(product)
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            productDao.updateProduct(product)
        }
    }

    fun deactivateProduct(productId: String) {
        viewModelScope.launch {
            productDao.deactivateProduct(productId)
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            productDao.searchProducts(query).collect { products ->
                _state.update { it.copy(products = products, isLoading = false) }
            }
        }
    }
}