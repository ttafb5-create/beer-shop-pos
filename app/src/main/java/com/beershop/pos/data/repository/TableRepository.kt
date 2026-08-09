package com.beershop.pos.data.repository

import com.beershop.pos.data.local.dao.*
import com.beershop.pos.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepository @Inject constructor(
    private val tableDao: TableDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) {
    fun getAllTables(): Flow<List<TableEntity>> = tableDao.getAllTables()
    fun getTablesByStatus(status: String): Flow<List<TableEntity>> = tableDao.getTablesByStatus(status)
    fun getTablesByZone(zone: String): Flow<List<TableEntity>> = tableDao.getTablesByZone(zone)
    fun getZones(): Flow<List<String>> = tableDao.getZones()
    suspend fun getTableById(tableId: String) = tableDao.getTableById(tableId)

    suspend fun createTable(tableNumber: String, tableName: String, capacity: Int, zone: String): TableEntity {
        val table = TableEntity(
            tableNumber = tableNumber,
            tableName = tableName.ifEmpty { "Table $tableNumber" },
            capacity = capacity,
            zone = zone
        )
        tableDao.insertTable(table)
        return table
    }

    suspend fun updateTable(table: TableEntity) {
        tableDao.updateTable(table.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteTable(tableId: String) = tableDao.deactivateTable(tableId)

    suspend fun openTable(tableId: String, orderId: String) {
        tableDao.assignOrderToTable(tableId, orderId, TableStatus.OCCUPIED)
    }

    suspend fun closeTable(tableId: String) {
        tableDao.updateTableStatus(tableId, TableStatus.AVAILABLE)
    }

    suspend fun holdTable(tableId: String) {
        tableDao.updateTableStatus(tableId, TableStatus.HELD)
    }

    suspend fun reserveTable(tableId: String) {
        tableDao.updateTableStatus(tableId, TableStatus.RESERVED)
    }

    suspend fun transferOrder(fromTableId: String, toTableId: String): Result<Boolean> {
        val order = orderDao.getOpenOrderForTable(fromTableId)
            ?: return Result.failure(Exception("No open order on table $fromTableId"))

        val targetOrder = orderDao.getOpenOrderForTable(toTableId)
        if (targetOrder != null) {
            return Result.failure(Exception("Destination table has an open order"))
        }

        orderDao.transferOrder(order.id, toTableId)
        tableDao.assignOrderToTable(fromTableId, null, TableStatus.AVAILABLE)
        tableDao.assignOrderToTable(toTableId, order.id, TableStatus.OCCUPIED)
        return Result.success(true)
    }

    suspend fun mergeTables(sourceTableId: String, targetTableId: String): Result<Boolean> {
        val sourceOrder = orderDao.getOpenOrderForTable(sourceTableId)
            ?: return Result.failure(Exception("No open order on source table"))
        val targetOrder = orderDao.getOpenOrderForTable(targetTableId)
            ?: return Result.failure(Exception("No open order on target table"))

        // Move all items from source to target
        val sourceItems = orderItemDao.getItemsForOrderSync(sourceOrder.id)
        val newItems = sourceItems.map { it.copy(
            id = UUID.randomUUID().toString(),
            orderId = targetOrder.id,
            sortOrder = it.sortOrder + 1000
        )}
        orderItemDao.insertItems(newItems)
        orderDao.markAsMerged(sourceOrder.id)
        tableDao.updateTableStatus(sourceTableId, TableStatus.AVAILABLE)

        // Recalculate target order
        val allItems = orderItemDao.getItemsForOrderSync(targetOrder.id)
        recalculateOrder(targetOrder.id, allItems)
        return Result.success(true)
    }

    suspend fun splitBill(tableId: String, itemIds: List<String>, newTableId: String): Result<String> {
        val originalOrder = orderDao.getOpenOrderForTable(tableId)
            ?: return Result.failure(Exception("No open order on table"))

        val allItems = orderItemDao.getItemsForOrderSync(originalOrder.id)
        val splitItems = allItems.filter { it.id in itemIds }
        val remainingItems = allItems.filter { it.id !in itemIds }

        if (splitItems.isEmpty()) return Result.failure(Exception("No items selected to split"))

        // Create new order on new table
        val datePart = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val orderCount = orderDao.getDailyOrderCount()
        val newOrder = OrderEntity(
            tableId = newTableId,
            orderNumber = "ORD-$datePart-${(orderCount + 1).toString().padStart(3, '0')}",
            cashierId = originalOrder.cashierId,
            cashierName = originalOrder.cashierName
        )
        orderDao.insertOrder(newOrder)

        // Move split items
        val movedItems = splitItems.map { it.copy(
            id = UUID.randomUUID().toString(),
            orderId = newOrder.id
        )}
        orderItemDao.insertItems(movedItems)
        splitItems.forEach { orderItemDao.deleteItem(it.id) }

        // Recalculate both orders
        recalculateOrder(originalOrder.id, remainingItems)
        recalculateOrder(newOrder.id, orderItemDao.getItemsForOrderSync(newOrder.id))

        tableDao.assignOrderToTable(newTableId, newOrder.id, TableStatus.OCCUPIED)
        return Result.success(newOrder.id)
    }

    private suspend fun recalculateOrder(orderId: String, items: List<OrderItemEntity>) {
        val subtotal = items.sumOf { (it.unitPrice * it.quantity) - it.discountAmount }
        val totalDiscount = items.sumOf { it.discountAmount }
        val totalTax = items.sumOf { it.taxAmount }
        val totalQty = items.sumOf { it.quantity }

        orderDao.updateOrderTotals(
            orderId = orderId,
            subtotal = subtotal,
            discount = totalDiscount,
            tax = totalTax,
            sc = 0.0,  // Will be recalculated
            total = subtotal - totalDiscount + totalTax,
            qty = totalQty
        )
    }

    suspend fun getOpenOrderForTable(tableId: String) = orderDao.getOpenOrderForTable(tableId)
    suspend fun getTableCountByStatus(status: String) = tableDao.getTableCountByStatus(status)
}
