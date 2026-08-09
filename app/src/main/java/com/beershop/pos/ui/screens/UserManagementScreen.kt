package com.beershop.pos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.beershop.pos.ui.viewmodel.UserManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onBack: () -> Unit,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val users = state.users

    var showAddUser by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newDisplayName by remember { mutableStateOf("") }
    var newRole by remember { mutableStateOf("CASHIER") }

    val roleColors = mapOf(
        "OWNER" to Color(0xFFE53935),
        "MANAGER" to Color(0xFF1565C0),
        "CASHIER" to Color(0xFF4CAF50)
    )
    val roleIcons = mapOf(
        "OWNER" to Icons.Default.AdminPanelSettings,
        "MANAGER" to Icons.Default.ManageAccounts,
        "CASHIER" to Icons.Default.Person
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management / \u1021\u101e\u102f\u1036\u1038\u1015\u103c\u102f\u101e\u1030\u1019\u103b\u102c\u1038") },
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
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (users.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No users yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showAddUser = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add User")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(users, key = { it.id }) { user ->
                    val color = roleColors[user.role] ?: Color.Gray
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                roleIcons[user.role] ?: Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = color
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    user.displayName.ifBlank { user.username },
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    user.role.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = color
                                )
                            }
                            if (user.role != "OWNER") {
                                IconButton(onClick = { viewModel.deleteUser(user.id) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFE53935)
                                    )
                                }
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
            title = { Text("Add User / \u1021\u101e\u102f\u1036\u1038\u1015\u103c\u102f\u101e\u1030\u1021\u101e\u1005\u103a") },
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
                        viewModel.addUser(newUsername, newPassword, newDisplayName, newRole)
                        showAddUser = false
                        newUsername = ""
                        newPassword = ""
                        newDisplayName = ""
                        newRole = "CASHIER"
                    }
                }) {
                    Text("Add / \u1011\u100a\u1037\u103a\u1019\u100a\u103a")
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
