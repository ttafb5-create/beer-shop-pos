package com.beershop.pos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beershop.pos.sync.SyncManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onPrinterSettings: () -> Unit,
    onUserManagement: () -> Unit,
    onSyncStatus: () -> Unit
) {
    var shopName by remember { mutableStateOf("Beer Shop") }
    var shopAddress by remember { mutableStateOf("No. 123, Main Street") }
    var shopPhone by remember { mutableStateOf("09-123456789") }
    var taxRate by remember { mutableStateOf("5") }
    var serviceCharge by remember { mutableStateOf("10") }
    var currency by remember { mutableStateOf("Ks (Myanmar Kyat)") }
    var language by remember { mutableStateOf("my") }
    var darkMode by remember { mutableStateOf(false) }
    var cloudSync by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings / ဆက်တင်များ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Save settings
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Shop Info
            item {
                Text("Shop Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Shop Name / ဆိုင်အမည်") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = shopAddress,
                    onValueChange = { shopAddress = it },
                    label = { Text("Address / လိပ်စာ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = shopPhone,
                    onValueChange = { shopPhone = it },
                    label = { Text("Phone / ဖုန်း") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Tax & Charges
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tax & Charges", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = taxRate,
                        onValueChange = { taxRate = it },
                        label = { Text("Tax Rate %") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        trailingIcon = { Text("%") }
                    )
                    OutlinedTextField(
                        value = serviceCharge,
                        onValueChange = { serviceCharge = it },
                        label = { Text("Service Charge %") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        trailingIcon = { Text("%") }
                    )
                }
            }

            // Printer
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Printing", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                SettingsItem(
                    title = "Printer Settings",
                    subtitle = "Bluetooth thermal printer",
                    icon = Icons.Default.Print,
                    onClick = onPrinterSettings
                )
            }

            // Users
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Users", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                SettingsItem(
                    title = "User Management",
                    subtitle = "Manage staff accounts",
                    icon = Icons.Default.People,
                    onClick = onUserManagement
                )
            }

            // Sync
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cloud Sync", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cloud Sync / အွန်လိုင်းသိမ်းဆည်း")
                    Switch(
                        checked = cloudSync,
                        onCheckedChange = { cloudSync = it }
                    )
                }
            }
            item {
                SettingsItem(
                    title = "Sync Status",
                    subtitle = "Check sync status",
                    icon = Icons.Default.CloudSync,
                    onClick = onSyncStatus
                )
            }

            // Display
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Display", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode / အမှောင်မုဒ်")
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
                    )
                }
            }
            item {
                Text(
                    "Language / ဘာသာစကား",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = language == "my",
                        onClick = { language = "my" },
                        label = { Text("မြန်မာ") }
                    )
                    FilterChip(
                        selected = language == "en",
                        onClick = { language = "en" },
                        label = { Text("English") }
                    )
                }
            }

            // Backup
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Backup */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Backup")
                    }
                    OutlinedButton(
                        onClick = { /* Restore */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restore")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "Beer Shop POS v1.0.0",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    "Phase 1 - Core POS",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
