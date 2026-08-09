package com.beershop.pos.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beershop.pos.data.local.entity.*
import com.beershop.pos.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentState(
    val order: OrderEntity? = null,
    val orderItems: List<OrderItemEntity> = emptyList(),
    val table: TableEntity? = null,
    val payments: List<PaymentEntity> = emptyList(),
    val totalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val remainingBalance: Double = 0.0,
    val change: Double = 0.0,
    val selectedMethod: String = PaymentMethod.CASH,
    val enteredAmount: String = "",
    val referenceNumber: String = "",
    val note: String = "",
    val mixedPayments: List<MixedPayment> = emptyList(),
    val isProcessing: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null
)

data class MixedPayment(
    val method: String,
    val amount: Double,
    val reference: String = ""
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val tableRepository: TableRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: String = savedStateHandle.get<String>("orderId") ?: ""

    private val _state = MutableStateFlow(PaymentState())
    val state: StateFlow<PaymentState> = _state.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            val order = orderRepository.getOrderById(orderId)
            _state.update {
                it.copy(
                    order = order,
                    totalAmount = order?.grandTotal ?: 0.0,
                    remainingBalance = order?.grandTotal ?: 0.0
                )
            }
            val table = tableRepository.getTableById(order?.tableId ?: "")
            _state.update { it.copy(table = table) }

            val items = orderRepository.getOrderItemsSync(orderId)
            _state.update { it.copy(orderItems = items) }
        }
    }

    fun setMethod(method: String) {
        _state.update { it.copy(selectedMethod = method) }
    }

    fun setEnteredAmount(amount: String) {
        val enteredAmount = amount.toDoubleOrNull() ?: 0.0
        val totalAmount = _state.value.totalAmount
        val change = if (enteredAmount > totalAmount) enteredAmount - totalAmount else 0.0
        _state.update {
            it.copy(
                enteredAmount = amount,
                paidAmount = enteredAmount,
                remainingBalance = (totalAmount - enteredAmount).coerceAtLeast(0.0),
                change = change
            )
        }
    }

    fun addMixedPayment(method: String, amount: Double, reference: String = "") {
        val mixedPayments = _state.value.mixedPayments.toMutableList()
        mixedPayments.add(MixedPayment(method, amount, reference))
        val totalPaid = mixedPayments.sumOf { it.amount }
        val total = _state.value.totalAmount
        _state.update {
            it.copy(
                mixedPayments = mixedPayments,
                paidAmount = totalPaid,
                remainingBalance = (total - totalPaid).coerceAtLeast(0.0)
            )
        }
    }

    fun removeMixedPayment(index: Int) {
        val mixedPayments = _state.value.mixedPayments.toMutableList()
        mixedPayments.removeAt(index)
        val totalPaid = mixedPayments.sumOf { it.amount }
        val total = _state.value.totalAmount
        _state.update {
            it.copy(
                mixedPayments = mixedPayments,
                paidAmount = totalPaid,
                remainingBalance = (total - totalPaid).coerceAtLeast(0.0)
            )
        }
    }

    fun processPayment(cashierId: String, cashierName: String) {
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true, error = null) }

            try {
                val mixedPayments = _state.value.mixedPayments
                if (mixedPayments.isNotEmpty()) {
                    val pairs = mixedPayments.map { it.method to it.amount }
                    paymentRepository.processMixedPayment(orderId, pairs, cashierId, cashierName)
                } else {
                    val amount = _state.value.enteredAmount.toDoubleOrNull()
                        ?: _state.value.totalAmount
                    paymentRepository.processPayment(
                        orderId = orderId,
                        method = _state.value.selectedMethod,
                        amount = amount,
                        referenceNumber = _state.value.referenceNumber,
                        note = _state.value.note,
                        cashierId = cashierId,
                        cashierName = cashierName
                    )
                }

                orderRepository.closeOrder(orderId)
                _state.value.table?.let {
                    tableRepository.closeTable(it.id)
                }

                _state.update { it.copy(isProcessing = false, isComplete = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isProcessing = false, error = e.message)
                }
            }
        }
    }

    fun setReferenceNumber(ref: String) {
        _state.update { it.copy(referenceNumber = ref) }
    }

    fun setNote(note: String) {
        _state.update { it.copy(note = note) }
    }
}
