package com.beershop.pos.data.repository

import com.beershop.pos.data.local.dao.OrderDao
import com.beershop.pos.data.local.dao.OrderItemDao
import com.beershop.pos.data.local.dao.ProductDao
import com.beershop.pos.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val productDao: ProductDao
) {
    suspend fun createOrder(
        tableId: String,
        cashierId: String,
        cashierName: String
    ): OrderEntity {
        val datePart = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val orderCount = orderDao.getDailyOrderCount()
        val order = OrderEntity(
            tableId = tableId,
            orderNumber = "ORD-$datePart-${(orderCount + 1).toString().padStart(3, '0')}",
            cashierId = cashierId,
            cashierName = cashierName
        )
        orderDao.insertOrder(order)
        return order
    }

    suspend fun getOrderById(orderId: String) = orderDao.getOrderById(orderId)
    suspend fun getOpenOrderForTable(tableId: String) = orderDao.getOpenOrderForTable(tableId)
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = orderItemDao.getItemsForOrder(orderId)
    suspend fun getOrderItemsSync(orderId: String) = orderItemDao.getItemsForOrderSync(orderId)
    fun getOpenOrders(): Flow<List<OrderEntity>> = orderDao.getOpenOrders()
    fun getOrdersByDate(date: Long = System.currentTimeMillis()): Flow<List<OrderEntity>> = orderDao.getOrdersByDate(date)

    suspend fun addItemToOrder(
        orderId: String,
        product: ProductEntity,
        quantity: Int = 1,
        note: String = ""
    ): OrderItemEntity {
        // Check if product already in order
        val existingItems = orderItemDao.getItemsForOrderSync(orderId)
        val existing = existingItems.find { it.productId == product.id && !it.isVoid }

        if (existing != null) {
            val newQty = existing.quantity + quantity
            val totalPrice = (product.sellingPrice * newQty) - existing.discountAmount + existing.taxAmount
            orderItemDao.updateItemQuantity(existing.id, newQty, totalPrice)
            recalculateOrder(orderId)
            return existing.copy(quantity = newQty, totalPrice = totalPrice)
        }

        val itemCount = existingItems.size
        val taxAmount = product.sellingPrice * quantity * (product.taxRate / 100.0)
        val totalPrice = (product.sellingPrice * quantity) + taxAmount

        val item = OrderItemEntity(
            orderId = orderId,
            productId = product.id,
            productName = product.name,
            productNameMyanmar = product.nameMyanmar,
            category = product.category,
            unitPrice = product.sellingPrice,
            quantity = quantity,
            taxAmount = taxAmount,
            taxPercent = product.taxRate,
            totalPrice = totalPrice,
            note = note,
            sortOrder = itemCount
        )
        orderItemDao.insertItem(item)
        recalculateOrder(orderId)
        return item
    }

    suspend fun updateItemQuantity(orderId: String, itemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            orderItemDao.deleteItem(itemId)
        } else {
            val item = orderItemDao.getItemById(itemId) ?: return
            val taxAmount = item.unitPrice * newQuantity * (item.taxPercent / 100.0)
            val totalPrice = (item.unitPrice * newQuantity) - item.discountAmount + taxAmount
            orderItemDao.updateItemQuantity(itemId, newQuantity, totalPrice)
        }
        recalculateOrder(orderId)
    }

    suspend fun removeItem(orderId: String, itemId: String) {
        orderItemDao.deleteItem(itemId)
        recalculateOrder(orderId)
    }

    suspend fun voidItem(orderId: String, itemId: String, reason: String = "") {
        orderItemDao.voidItem(itemId, reason)
        recalculateOrder(orderId)
    }

    suspend fun updateItemNote(itemId: String, note: String) {
        orderItemDao.updateItemNote(itemId, note)
    }

    suspend fun applyOrderDiscount(orderId: String, discountPercent: Double, discountAmount: Double = 0.0, note: String = "") {
        val order = orderDao.getOrderById(orderId) ?: return
        val items = orderItemDao.getItemsForOrderSync(orderId)

        val totalDiscountAmount = if (discountPercent > 0) {
            order.subtotal * (discountPercent / 100.0)
        } else {
            discountAmount
        }

        orderDao.updateOrder(order.copy(
            discountPercent = discountPercent,
            discountAmount = totalDiscountAmount,
            discountNote = note,
            updatedAt = System.currentTimeMillis()
        ))
        recalculateOrder(orderId)
    }

    suspend fun saveHoldOrder(orderId: String) {
        orderDao.updateOrderStatus(orderId, OrderStatus.HELD)
    }

    suspend fun resumeHeldOrder(orderId: String) {
        orderDao.updateOrderStatus(orderId, OrderStatus.OPEN)
    }

    suspend fun closeOrder(orderId: String) {
        orderDao.updateOrderStatus(
            orderId = orderId,
            status = OrderStatus.CLOSED,
            closedAt = System.currentTimeMillis()
        )
    }

    suspend fun reopenOrder(orderId: String) {
        orderDao.updateOrderStatus(
            orderId = orderId,
            status = OrderStatus.OPEN,
            closedAt = null
        )
    }

    suspend fun voidOrder(orderId: String) {
        orderDao.updateOrderStatus(orderId, OrderStatus.VOID)
    }

    private suspend fun recalculateOrder(orderId: String) {
        val items = orderItemDao.getItemsForOrderSync(orderId)
        val subtotal = items.sumOf { it.unitPrice * it.quantity }
        val totalDiscount = items.sumOf { it.discountAmount }
        val totalTax = items.sumOf { it.taxAmount }
        val totalQty = items.sumOf { it.quantity }

        val order = orderDao.getOrderById(orderId) ?: return
        val serviceCharge = subtotal * (order.serviceChargePercent / 100.0)
        val orderDiscount = order.discountAmount
        val grandTotal = subtotal + totalTax + serviceCharge - orderDiscount

        orderDao.updateOrderTotals(
            orderId = orderId,
            subtotal = subtotal,
            discount = orderDiscount,
            tax = totalTax,
            sc = serviceCharge,
            total = grandTotal,
            qty = totalQty
        )
    }

    suspend fun getSalesByProductDaily(date: Long = System.currentTimeMillis()) =
        orderItemDao.getSalesByProductDaily(date)
}
