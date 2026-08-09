package com.beershop.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.beershop.pos.data.local.entity.TableEntity
import com.beershop.pos.data.local.entity.TableStatus
import com.beershop.pos.ui.viewmodel.TableManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableManagementScreen(
    onTableClick: (String, String?) -> Unit,
    onBack: () -> Unit,
    viewModel: TableManagementViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val tables = state.tables

    var selectedZone by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var newTableNumber by remember { mutableStateOf("") }
    var newTableName by remember { mutableStateOf("") }
    var newTableZone by remember { mutableStateOf("Main") }

    // Filter tables by zone locally
    val filteredTables = if (selectedZone == "All") tables
        else tables.filter { it.zone == selectedZone }

    val allZones = remember(tables) { 
        (listOf("All") + tables.map { it.zone }.distinct().sorted()).toMutableStateList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Table Management / \u1005\u102c\u1038\u1015\u103d\u1032\u1019\u103b\u102c\u1038") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Table")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Zone filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allZones.forEach { zone ->
                    FilterChip(
                        selected = selectedZone == zone,
                        onClick = { selectedZone = zone },
                        label = { Text(zone) }
                    )
                }
            }

            // Stats bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusBadge("Available", Color(0xFF4CAF50), filteredTables.count { it.status == TableStatus.AVAILABLE })
                StatusBadge("Occupied", Color(0xFFFF5722), filteredTables.count { it.status == TableStatus.OCCUPIED })
                StatusBadge("Reserved", Color(0xFFFFC107), filteredTables.count { it.status == TableStatus.RESERVED })
                StatusBadge("Held", Color(0xFF9C27B0), filteredTables.count { it.status == TableStatus.HELD })
            }
            Divider()

            if (filteredTables.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.TableRestaurant,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No tables yet / \u1005\u102c\u1038\u1015\u103d\u1032\u1019\u101b\u103e\u102d\u101e\u1038",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add First Table")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTables, key = { it.id }) { table ->
                        TableCard(
                            table = table,
                            onClick = {
                                onTableClick(table.id, table.currentOrderId)
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Table Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Table / \u1005\u102c\u1038\u1015\u103d\u1032\u1021\u101e\u1005\u103a") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newTableNumber,
                        onValueChange = { newTableNumber = it },
                        label = { Text("Table Number / \u1005\u102c\u1038\u1015\u103d\u1032\u1014\u1036\u1015\u102c\u1010\u103a") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTableName,
                        onValueChange = { newTableName = it },
                        label = { Text("Table Name / \u1005\u102c\u1038\u1015\u103d\u1032\u1021\u1019\u100a\u103a (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTableZone,
                        onValueChange = { newTableZone = it },
                        label = { Text("Zone / \u1007\u102f\u1014\u103a") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newTableNumber.isNotBlank()) {
                        viewModel.addTable(newTableNumber, newTableName, newTableZone)
                        showAddDialog = false
                        newTableNumber = ""
                        newTableName = ""
                        newTableZone = "Main"
                    }
                }) {
                    Text("Add / \u1011\u100a\u1037\u103a\u1019\u100a\u103a")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel / \u1019\u101c\u102f\u1015\u103a\u1010\u1031\u102c\u1037\u1015\u102b")
                }
            }
        )
    }
}

@Composable
fun TableCard(
    table: TableEntity,
    onClick: () -> Unit
) {
    val statusColor = when (table.status) {
        TableStatus.AVAILABLE -> Color(0xFF4CAF50)
        TableStatus.OCCUPIED -> Color(0xFFFF5722)
        TableStatus.RESERVED -> Color(0xFFFFC107)
        TableStatus.HELD -> Color(0xFF9C27B0)
        else -> Color(0xFF9E9E9E)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(statusColor)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.TableRestaurant,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = table.tableNumber,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (table.tableName.isNotBlank()) {
                    Text(
                        text = table.tableName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = table.status.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = "${table.capacity} pax",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusBadge(label: String, color: Color, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "$label: $count",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}