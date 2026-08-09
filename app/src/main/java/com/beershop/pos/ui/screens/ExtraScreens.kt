package com.beershop.pos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterSettingsScreen(
    onBack: () -> Unit
) {
    var selectedPrinter by remember { mutableStateOf("") }
    var paperWidth by remember { mutableStateOf("58") }
    var isLoading by remember { mutableStateOf(false) }
    val pairedPrinters = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Printer Settings / ပရင်တာ ဆက်တင်များ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Bluetooth Printer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Scan and select a thermal printer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                isLoading = true
                                // Scan for printers
                                isLoading = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.BluetoothSearching, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan Printers / ရှာဖွေမည်")
                        }
                    }
                }
            }

            if (pairedPrinters.isNotEmpty()) {
                item {
                    Text(
                        "Paired Printers:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(pairedPrinters.size) { index ->
                    val printer = pairedPrinters[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPrinter == printer)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Print,
                                    contentDescription = null,
                                    tint = if (selectedPrinter == printer)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(printer, style = MaterialTheme.typography.bodyMedium)
                            }
                            if (selectedPrinter == printer) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Paper Size",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilterChip(
                                selected = paperWidth == "58",
                                onClick = { paperWidth = "58" },
                                label = { Text("58mm") },
                                leadingIcon = {
                                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                            FilterChip(
                                selected = paperWidth == "80",
                                onClick = { paperWidth = "80" },
                                label = { Text("80mm") },
                                leadingIcon = {
                                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { /* Test print */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedPrinter.isNotBlank()
                ) {
                    Icon(Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Print / စမ်းသပ်ရိုက်မည်")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncStatusScreen(
    onBack: () -> Unit
) {
    var isOnline by remember { mutableStateOf(false) }
    var syncInProgress by remember { mutableStateOf(false) }
    var pendingCount by remember { mutableStateOf(0) }
    var lastSyncTime by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sync Status / ထပ်တူပြုမှုအခြေအနေ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = if (isOnline) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            if (isOnline) "Online" else "Offline",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                        )
                        Text(
                            if (isOnline) "Connected to cloud" else "Working in offline mode",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Sync Stats", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        SyncStatRow("Pending Sync", "$pendingCount items", Color(0xFFEF6C00))
                        SyncStatRow("Last Sync", if (lastSyncTime != null) "2 min ago" else "Never", Color(0xFF9E9E9E))
                        SyncStatRow("Status", if (syncInProgress) "Syncing..." else "Idle",
                            if (syncInProgress) Color(0xFF1565C0) else Color(0xFF4CAF50))
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        syncInProgress = true
                        // Trigger sync
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !syncInProgress
                ) {
                    if (syncInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Sync, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (syncInProgress) "Syncing..." else "Sync Now / ထပ်တူပြုမည်")
                }
            }
        }
    }
}

@Composable
fun SyncStatRow(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onBack: () -> Unit
) {
    var users by remember { mutableStateOf(listOf<String>()) }
    var showAddUser by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newDisplayName by remember { mutableStateOf("") }
    var newRole by remember { mutableStateOf("CASHIER") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management / အသုံးပြုသူများ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddUser = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add User")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null,
                                modifier = Modifier.size(40.dp), tint = Color(0xFFE53935))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("admin", fontWeight = FontWeight.Bold)
                                Text("Owner", style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE53935))
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ManageAccounts, contentDescription = null,
                                modifier = Modifier.size(40.dp), tint = Color(0xFF1565C0))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("manager", fontWeight = FontWeight.Bold)
                                Text("Manager", style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1565C0))
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null,
                                modifier = Modifier.size(40.dp), tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("cashier", fontWeight = FontWeight.Bold)
                                Text("Cashier", style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF4CAF50))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddUser) {
        AlertDialog(
            onDismissRequest = { showAddUser = false },
            title = { Text("Add User / အသုံးပြုသူအသစ်") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = { newUsername = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDisplayName,
                        onValueChange = { newDisplayName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Role:", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("OWNER" to "Owner", "MANAGER" to "Manager", "CASHIER" to "Cashier").forEach { (role, label) ->
                            FilterChip(
                                selected = newRole == role,
                                onClick = { newRole = role },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newUsername.isNotBlank() && newPassword.isNotBlank()) {
                        showAddUser = false
                    }
                }) {
                    Text("Add / ထည့်မည်")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUser = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
