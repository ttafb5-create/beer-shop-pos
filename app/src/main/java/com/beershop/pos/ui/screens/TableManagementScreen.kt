package com.beershop.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.beershop.pos.data.repository.TableRepository
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableManagementScreen(
    onTableClick: (String, String?) -> Unit,
    onBack: () -> Unit) {
    // For now, we'll use Compose state directly with table lists
    var tables by remember { mutableStateOf<List<TableEntity>>(emptyList()) }
    var selectedZone by remember { mutableStateOf("All") }
    var zones by remember { mutableStateOf(listOf("All")) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var showMergeDialog by remember { mutableStateOf(false) }
    var selectedTable by remember { mutableStateOf<TableEntity?>(null) }

    var newTableNumber by remember { mutableStateOf("") }
    var newTableName by remember { mutableStateOf("") }
    var newTableZone by remember { mutableStateOf("Main") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Table Management / စားပွဲများ") },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        zones.forEach { zone ->
                            FilterChip(
                                selected = selectedZone == zone,
                                onClick = { selectedZone = zone },
                                label = { Text(zone) }
                            )
                        }
                    }
                }
            }

            // Stats bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusBadge("Available", Color(0xFF4CAF50), tables.count { it.status == TableStatus.AVAILABLE })
                StatusBadge("Occupied", Color(0xFFFF5722), tables.count { it.status == TableStatus.OCCUPIED })
                StatusBadge("Reserved", Color(0xFFFFC107), tables.count { it.status == TableStatus.RESERVED })
                StatusBadge("Held", Color(0xFF9C27B0), tables.count { it.status == TableStatus.HELD })
            }
            Divider()

            // Table Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tables) { table ->
                    TableCard(
                        table = table,
                        onClick = {
                            onTableClick(table.id, table.currentOrderId)
                        },
                        onLongClick = {
                            selectedTable = table
                            // Show context menu
                        }
                    )
                }
            }
        }
    }

    // Add Table Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Table / စားပွဲအသစ်") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newTableNumber,
                        onValueChange = { newTableNumber = it },
                        label = { Text("Table Number / စားပွဲနံပါတ်") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTableName,
                        onValueChange = { newTableName = it },
                        label = { Text("Table Name / စားပွဲအမည် (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTableZone,
                        onValueChange = { newTableZone = it },
                        label = { Text("Zone / ဇုန်") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newTableNumber.isNotBlank()) {
                        // tableRepository.createTable(newTableNumber, newTableName, 4, newTableZone)
                        showAddDialog = false
                        newTableNumber = ""
                        newTableName = ""
                    }
                }) {
                    Text("Add / ထည့်မည်")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel / မလုပ်တော့ပါ")
                }
            }
        )
    }
}

@Composable
fun TableCard(
    table: TableEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val statusColor = when (table.status) {
        TableStatus.AVAILABLE -> Color(0xFF4CAF50)
        TableStatus.OCCUPIED -> Color(0xFFFF5722)
        TableStatus.RESERVED -> Color(0xFFFFC107)
        TableStatus.HELD -> Color(0xFF9C27B0)
        TableStatus.CLOSED -> Color(0xFF9E9E9E)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status bar
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
