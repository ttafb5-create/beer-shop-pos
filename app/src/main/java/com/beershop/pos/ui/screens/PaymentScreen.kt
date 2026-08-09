package com.beershop.pos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.beershop.pos.data.local.entity.PaymentMethod
import com.beershop.pos.ui.viewmodel.PaymentViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }
    var showMixedPayment by remember { mutableStateOf(false) }
    var mixedMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var mixedAmount by remember { mutableStateOf("") }
    var mixedReference by remember { mutableStateOf("") }

    if (state.isComplete) {
        // Payment complete screen
        PaymentCompleteScreen(
            order = state.order,
            totalAmount = state.totalAmount,
            numberFormat = numberFormat,
            onDone = onComplete
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment / ငွေရှင်းရန်") },
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
            // Order Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Total Amount / စုစုပေါင်း",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Ks ${numberFormat.format(state.totalAmount.toLong())}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 36.sp
                        )
                        if (state.table != null) {
                            Text(
                                "Table: ${state.table?.tableNumber} | Order: ${state.order?.orderNumber}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Payment Methods
            item {
                Text(
                    "Payment Method / ငွေပေးချေနည်းလမ်း",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!showMixedPayment) {
                // Single payment method grid
                val methods = listOf(
                    Triple(PaymentMethod.CASH, "Cash", Icons.Default.Money),
                    Triple(PaymentMethod.KBZPAY, "KBZPay", Icons.Default.AccountBalanceWallet),
                    Triple(PaymentMethod.WAVE_MONEY, "Wave Pay", Icons.Default.PhoneAndroid),
                    Triple(PaymentMethod.AYA_PAY, "AYA Pay", Icons.Default.Payment),
                    Triple(PaymentMethod.CB_PAY, "CB Pay", Icons.Default.CreditCard),
                    Triple(PaymentMethod.BANK_TRANSFER, "Bank", Icons.Default.AccountBalance),
                    Triple(PaymentMethod.OTHER, "Other", Icons.Default.MoreHoriz)
                )

                itemsIndexed(methods.chunked(3)) { _, rowMethods ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowMethods.forEach { (method, label, icon) ->
                            PaymentMethodCard(
                                label = label,
                                icon = icon,
                                isSelected = state.selectedMethod == method,
                                onClick = { viewModel.setMethod(method) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill remaining space
                        repeat(3 - rowMethods.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Mixed Payment Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Mixed Payment / ပေါင်းစပ်ငွေပေးချေမှု",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Switch(
                        checked = showMixedPayment,
                        onCheckedChange = { showMixedPayment = it }
                    )
                }
            }

            // Amount Entry (Single Payment)
            if (!showMixedPayment) {
                item {
                    OutlinedTextField(
                        value = state.enteredAmount,
                        onValueChange = { viewModel.setEnteredAmount(it) },
                        label = { Text("Amount Received / ရရှိငွေ") },
                        leadingIcon = { Text("Ks", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    if (state.selectedMethod != PaymentMethod.CASH) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.referenceNumber,
                            onValueChange = { viewModel.setReferenceNumber(it) },
                            label = { Text("Reference Number / ကိုးကားနံပါတ်") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // Mixed Payment Entries
            if (showMixedPayment) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Method selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PaymentMethod.ALL.forEach { method ->
                                    FilterChip(
                                        selected = mixedMethod == method,
                                        onClick = { mixedMethod = method },
                                        label = {
                                            Text(
                                                when (method) {
                                                    PaymentMethod.CASH -> "Cash"
                                                    else -> method.replace("_", " ")
                                                },
                                                fontSize = 11.sp
                                            )
                                        },
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = mixedAmount,
                                onValueChange = { mixedAmount = it },
                                label = { Text("Amount / ပမာဏ") },
                                leadingIcon = { Text("Ks") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (mixedMethod != PaymentMethod.CASH) {
                                OutlinedTextField(
                                    value = mixedReference,
                                    onValueChange = { mixedReference = it },
                                    label = { Text("Reference (optional)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Button(
                                onClick = {
                                    val amount = mixedAmount.toDoubleOrNull() ?: 0.0
                                    if (amount > 0) {
                                        viewModel.addMixedPayment(mixedMethod, amount, mixedReference)
                                        mixedAmount = ""
                                        mixedReference = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add Payment / ထည့်မည်")
                            }
                        }
                    }
                }
            }

            // Mixed Payment List
            if (state.mixedPayments.isNotEmpty()) {
                item {
                    Text(
                        "Added Payments:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                itemsIndexed(state.mixedPayments) { index, payment ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    PaymentMethod.displayName(payment.method),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Ks ${numberFormat.format(payment.amount.toLong())}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { viewModel.removeMixedPayment(index) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // Payment Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryRow("Total / စုစုပေါင်း", "Ks ${numberFormat.format(state.totalAmount.toLong())}")
                        SummaryRow("Paid / ပေးငွေ", "Ks ${numberFormat.format(state.paidAmount.toLong())}",
                            color = Color(0xFF2E7D32))
                        SummaryRow("Balance / ကျန်ငွေ", "Ks ${numberFormat.format(state.remainingBalance.toLong())}",
                            color = if (state.remainingBalance > 0) Color(0xFFE53935) else Color(0xFF2E7D32))
                        if (state.change > 0) {
                            SummaryRow("Change / အမ်းငွေ", "Ks ${numberFormat.format(state.change.toLong())}",
                                color = Color(0xFF1565C0), bold = true)
                        }
                    }
                }
            }

            if (state.error != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            state.error ?: "",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Process Payment Button
            item {
                Button(
                    onClick = {
                        viewModel.processPayment(
                            cashierId = "default",
                            cashierName = "Cashier"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    enabled = !state.isProcessing &&
                            (state.paidAmount >= state.totalAmount || state.mixedPayments.isNotEmpty())
                ) {
                    if (state.isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Complete Payment / ငွေရှင်းမည်",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            CardDefaults.outlinedCardBorder()
        else
            null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}

@Composable
fun PaymentCompleteScreen(
    order: com.beershop.pos.data.local.entity.OrderEntity?,
    totalAmount: Double,
    numberFormat: NumberFormat,
    onDone: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    "Payment Complete!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "ငွေရှင်းပြီးပါပြီ",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Ks ${numberFormat.format(totalAmount.toLong())}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (order != null) {
                    Text(
                        "Order: ${order.orderNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Done / ပြီးပါပြီ")
                }
            }
        }
    }
}
