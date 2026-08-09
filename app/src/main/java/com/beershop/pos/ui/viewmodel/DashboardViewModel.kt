package com.beershop.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beershop.pos.data.local.entity.*
import com.beershop.pos.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class DashboardState(
    val todaySales: Double = 0.0,
    val openTables: Int = 0,
    val closedTables: Int = 0,
    val totalOrders: Int = 0,
    val cashBalance: Double = 0.0,
    val walletBalance: Double = 0.0,
    val recentOrders: List<OrderEntity> = emptyList(),
    val salesBreakdown: List<com.beershop.pos.data.local.dao.PaymentDao.MethodBreakdown> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val tableRepository: TableRepository,
    private val paymentDao: com.beershop.pos.data.local.dao.PaymentDao
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val now = System.currentTimeMillis()

            val cashTotal = paymentRepository.getDailyTotalByMethod(PaymentMethod.CASH, now)
            val kpayTotal = paymentRepository.getDailyTotalByMethod(PaymentMethod.KBZPAY, now)
            val waveTotal = paymentRepository.getDailyTotalByMethod(PaymentMethod.WAVE_MONEY, now)
            val ayaTotal = paymentRepository.getDailyTotalByMethod(PaymentMethod.AYA_PAY, now)
            val cbTotal = paymentRepository.getDailyTotalByMethod(PaymentMethod.CB_PAY, now)
            val bankTotal = paymentRepository.getDailyTotalByMethod(PaymentMethod.BANK_TRANSFER, now)
            val otherTotal = paymentRepository.getDailyTotalByMethod(PaymentMethod.OTHER, now)

            val totalSales = paymentRepository.getDailyTotal(now)
            val walletSales = kpayTotal + waveTotal + ayaTotal + cbTotal
            val openTables = tableRepository.getTableCountByStatus(TableStatus.OCCUPIED)
            val closedTables = tableRepository.getTableCountByStatus(TableStatus.CLOSED)
            val heldTables = tableRepository.getTableCountByStatus(TableStatus.HELD)
            val breakdown = paymentRepository.getDailySalesBreakdown(now)

            _state.update {
                it.copy(
                    todaySales = totalSales,
                    openTables = openTables + heldTables,
                    closedTables = closedTables,
                    totalOrders = openTables + closedTables + heldTables,
                    cashBalance = cashTotal,
                    walletBalance = walletSales,
                    salesBreakdown = breakdown,
                    isLoading = false
                )
            }
        }
    }
}
