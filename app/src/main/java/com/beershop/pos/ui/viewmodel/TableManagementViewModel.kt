package com.beershop.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beershop.pos.data.local.dao.TableDao
import com.beershop.pos.data.local.entity.TableEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TableManagementState(
    val tables: List<TableEntity> = emptyList(),
    val zones: List<String> = emptyList(),
    val selectedZone: String = "All",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class TableManagementViewModel @Inject constructor(
    private val tableDao: TableDao
) : ViewModel() {

    private val _state = MutableStateFlow(TableManagementState())
    val state: StateFlow<TableManagementState> = _state.asStateFlow()

    init {
        loadTables()
        loadZones()
    }

    private fun loadTables() {
        viewModelScope.launch {
            tableDao.getAllTables().collect { tables ->
                _state.update { it.copy(tables = tables, isLoading = false) }
            }
        }
    }

    private fun loadZones() {
        viewModelScope.launch {
            tableDao.getZones().collect { zones ->
                _state.update { it.copy(zones = zones) }
            }
        }
    }

    fun filterByZone(zone: String) {
        _state.update { it.copy(selectedZone = zone) }
        viewModelScope.launch {
            if (zone == "All") {
                tableDao.getAllTables().collect { tables ->
                    _state.update { it.copy(tables = tables) }
                }
            } else {
                tableDao.getTablesByZone(zone).collect { tables ->
                    _state.update { it.copy(tables = tables) }
                }
            }
        }
    }

    fun addTable(tableNumber: String, tableName: String, zone: String, capacity: Int = 4) {
        viewModelScope.launch {
            val table = TableEntity(
                tableNumber = tableNumber,
                tableName = tableName,
                zone = zone,
                capacity = capacity
            )
            tableDao.insertTable(table)
        }
    }

    fun deleteTable(tableId: String) {
        viewModelScope.launch {
            tableDao.deactivateTable(tableId)
        }
    }
}