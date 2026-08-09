package com.beershop.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.beershop.pos.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.authState.collectAsState()
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var showPassword by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(state.error) {
        if (state.error != null) showError = true
    }

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // App Icon
            Text(
                text = "🍺",
                fontSize = 72.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Beer Shop POS",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "ဘီယာဆိုင် POS",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Login / ဝင်ရောက်ရန်",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    if (showError && state.error != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = state.error ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it; showError = false },
                        label = { Text("Username / အသုံးပြုသူအမည်") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; showError = false },
                        label = { Text("Password / စကားဝှက်") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (username.isNotBlank() && password.isNotBlank()) {
                                    viewModel.login(username, password)
                                }
                            }
                        )
                    )

                    Button(
                        onClick = {
                            if (username.isNotBlank() && password.isNotBlank()) {
                                viewModel.login(username, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Login / ဝင်ရောက်မည်",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    // Quick role info
                    Text(
                        text = "Owner: admin/admin123 | Cashier: cashier/cashier123",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "v1.0.0 - Phase 1",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            )
        }
    }
}
