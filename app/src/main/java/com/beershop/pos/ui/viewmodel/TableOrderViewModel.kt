package com.beershop.pos.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beershop.pos.data.local.entity.*
import com.beershop.pos.data.repository.*
import com.beershop.pos.ui.navigation.NavigationArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TableOrderState(
    val table: TableEntity? = null,
    val order: OrderEntity? = null,
    val orderItems: List<OrderItemEntity> = emptyList(),
    val availableProducts: List<ProductEntity> = emptyList(),
    val filteredProducts: List<ProductEntity> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TableOrderViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tableId: String = savedStateHandle.get<String>(NavigationArgs.TABLE_ID) ?: ""
    private val orderIdParam: String = savedStateHandle.get<String>(NavigationArgs.ORDER_ID) ?: "new"

    private val _state = MutableStateFlow(TableOrderState())
    val state: StateFlow<TableOrderState> = _state.asStateFlow()

    init {
        loadTableAndOrder()
        loadProducts()
    }

    private fun loadTableAndOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val table = tableRepository.getTableById(tableId)
            _state.update { it.copy(table = table) }

            var order = if (orderIdParam != "new") {
                orderRepository.getOrderById(orderIdParam)
            } else {
                tableRepository.getOpenOrderForTable(tableId)
            }

            var authVm: AuthViewModel? = null
            // Create order if needed
            if (order == null && table != null) {
                // Use default cashier if available
                val defaultCashierId = "default"
                val defaultCashierName = "Cashier"
                order = orderRepository.createOrder(tableId, defaultCashierId, defaultCashierName)
                tableRepository.openTable(tableId, order.id)
            }

            _state.update { it.copy(order = order, isLoading = false) }

            order?.let { loadOrderItems(it.id) }
        }
    }

    private fun loadOrderItems(orderId: String) {
        viewModelScope.launch {
            orderRepository.getOrderItems(orderId).collect { items ->
                _state.update { it.copy(orderItems = items) }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts().collect { products ->
                _state.update {
                    it.copy(
                        availableProducts = products,
                        filteredProducts = products
                    )
                }
            }
            productRepository.getCategories().collect { cats ->
                _state.update { it.copy(categories = cats) }
            }
        }
    }

    fun addProduct(product: ProductEntity, quantity: Int = 1) {
        val order = _state.value.order ?: return
        viewModelScope.launch {
            orderRepository.addItemToOrder(order.id, product, quantity)
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        val order = _state.value.order ?: return
        viewModelScope.launch {
            orderRepository.updateItemQuantity(order.id, itemId, newQuantity)
        }
    }

    fun removeItem(itemId: String) {
        val order = _state.value.order ?: return
        viewModelScope.launch {
            orderRepository.removeItem(order.id, itemId)
        }
    }

    fun voidItem(itemId: String, reason: String = "") {
        val order = _state.value.order ?: return
        viewModelScope.launch {
            orderRepository.voidItem(order.id, itemId, reason)
        }
    }

    fun updateItemNote(itemId: String, note: String) {
        viewModelScope.launch {
            orderRepository.updateItemNote(itemId, note)
        }
    }

    fun searchProducts(query: String) {
        _state.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            val category = _state.value.selectedCategory
            val products = if (category != null) {
                productRepository.searchProductsInCategory(query, category)
            } else {
                productRepository.searchProducts(query)
            }
            products.collect { results ->
                _state.update { it.copy(filteredProducts = results) }
            }
        }
    }

    fun filterByCategory(category: String?) {
        _state.update { it.copy(selectedCategory = category) }
        viewModelScope.launch {
            val query = _state.value.searchQuery
            val products = if (category != null) {
                if (query.isNotEmpty()) {
                    productRepository.searchProductsInCategory(query, category)
                } else {
                    productRepository.getProductsByCategory(category)
                }
            } else {
                if (query.isNotEmpty()) {
                    productRepository.searchProducts(query)
                } else {
                    productRepository.getAllProducts()
                }
            }
            products.collect { results ->
                _state.update { it.copy(filteredProducts = results) }
            }
        }
    }

    fun applyDiscount(discountPercent: Double, note: String = "") {
        val order = _state.value.order ?: return
        viewModelScope.launch {
            orderRepository.applyOrderDiscount(order.id, discountPercent, note = note)
        }
    }

    fun holdOrder() {
        val order = _state.value.order ?: return
        viewModelScope.launch {
            orderRepository.saveHoldOrder(order.id)
            tableRepository.holdTable(tableId)
        }
    }

    fun resumeOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.resumeHeldOrder(orderId)
            tableRepository.openTable(tableId, orderId)
        }
    }
}
