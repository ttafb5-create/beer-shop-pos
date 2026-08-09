package com.beershop.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.beershop.pos.data.local.entity.*
import com.beershop.pos.ui.viewmodel.TableOrderViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableOrderScreen(
    onBack: () -> Unit,
    onPayment: (String) -> Unit,
    onPrint: (String) -> Unit,
    viewModel: TableOrderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }
    var showDiscountDialog by remember { mutableStateOf(false) }
    var discountPercent by remember { mutableStateOf("") }
    var discountNote by remember { mutableStateOf("") }
    var showHoldConfirm by remember { mutableStateOf(false) }
    var showQuantityDialog by remember { mutableStateOf(false) }
    var quantityDialogItem by remember { mutableStateOf<OrderItemEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Table ${state.table?.tableNumber ?: ""}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (state.order != null) {
                            Text(
                                "${state.order?.orderNumber ?: ""} - ${state.orderItems.size} items",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDiscountDialog = true }) {
                        Icon(Icons.Default.Discount, contentDescription = "Discount")
                    }
                    IconButton(onClick = { showHoldConfirm = true }) {
                        Icon(Icons.Default.PauseCircle, contentDescription = "Hold Order")
                    }
                    IconButton(onClick = {
                        state.order?.let { onPrint(it.id) }
                    }) {
                        Icon(Icons.Default.Print, contentDescription = "Print")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            // Order Summary and Actions
            state.order?.let { order ->
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Totals
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Ks ${numberFormat.format(order.subtotal.toLong())}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (order.discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Discount:", style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFE53935))
                                Text(
                                    "-Ks ${numberFormat.format(order.discountAmount.toLong())}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFE53935)
                                )
                            }
                        }
                        if (order.serviceCharge > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Service Charge (${order.serviceChargePercent.toInt()}%):",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Ks ${numberFormat.format(order.serviceCharge.toLong())}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        if (order.taxAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tax:", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Ks ${numberFormat.format(order.taxAmount.toLong())}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Grand Total / စုစုပေါင်း",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            Text(
                                "Ks ${numberFormat.format(order.grandTotal.toLong())}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showHoldConfirm = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF9C27B0)
                                )
                            ) {
                                Icon(Icons.Default.PauseCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hold")
                            }
                            Button(
                                onClick = { onPayment(order.id) },
                                modifier = Modifier.weight(2f),
                                enabled = state.orderItems.isNotEmpty()
                            ) {
                                Icon(Icons.Default.Payments, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Payment / ငွေရှင်းမည်",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.searchProducts(it) },
                label = { Text("Search Products / ပစ္စည်းရှာရန်") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                singleLine = true
            )

            // Category Filter
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.selectedCategory == null,
                        onClick = { viewModel.filterByCategory(null) },
                        label = { Text("All / အားလုံး") }
                    )
                }
                items(state.categories) { category ->
                    FilterChip(
                        selected = state.selectedCategory == category,
                        onClick = { viewModel.filterByCategory(category) },
                        label = {
                            Text("${ProductCategory.emoji(category)} ${ProductCategory.displayName(category)}")
                        }
                    )
                }
            }

            // Split: Order Items (top) and Products (bottom)
            Row(modifier = Modifier.fillMaxSize()) {
                // Products Grid (left side, scrollable)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.filteredProducts) { product ->
                        ProductItemCard(
                            product = product,
                            numberFormat = numberFormat,
                            onClick = { viewModel.addProduct(product) }
                        )
                    }
                    if (state.filteredProducts.isEmpty()) {
                        item {
                            Text(
                                "No products found",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Divider()

                // Current Order Items (right side)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Text(
                            "Order Items",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(state.orderItems, key = { it.id }) { item ->
                        OrderItemCard(
                            item = item,
                            numberFormat = numberFormat,
                            onIncrease = { viewModel.updateQuantity(item.id, item.quantity + 1) },
                            onDecrease = {
                                if (item.quantity > 1) {
                                    viewModel.updateQuantity(item.id, item.quantity - 1)
                                } else {
                                    viewModel.removeItem(item.id)
                                }
                            },
                            onRemove = { viewModel.removeItem(item.id) }
                        )
                    }
                    if (state.orderItems.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    "Add products to this order\nပစ္စည်းများထည့်ပါ",
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Discount Dialog
    if (showDiscountDialog) {
        AlertDialog(
            onDismissRequest = { showDiscountDialog = false },
            title = { Text("Apply Discount / လျှော့စျေး") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = discountPercent,
                        onValueChange = { discountPercent = it },
                        label = { Text("Discount %") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = discountNote,
                        onValueChange = { discountNote = it },
                        label = { Text("Note (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val pct = discountPercent.toDoubleOrNull() ?: 0.0
                    if (pct > 0) {
                        viewModel.applyDiscount(pct, discountNote)
                    }
                    showDiscountDialog = false
                    discountPercent = ""
                    discountNote = ""
                }) {
                    Text("Apply / လျှော့မည်")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Hold Confirmation
    if (showHoldConfirm) {
        AlertDialog(
            onDismissRequest = { showHoldConfirm = false },
            title = { Text("Hold Order?") },
            text = { Text("This will pause the order and free the table.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.holdOrder()
                    showHoldConfirm = false
                }) {
                    Text("Hold")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHoldConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProductItemCard(
    product: ProductEntity,
    numberFormat: NumberFormat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (product.nameMyanmar.isNotBlank()) {
                    Text(
                        text = product.nameMyanmar,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${ProductCategory.emoji(product.category)} Ks ${numberFormat.format(product.sellingPrice.toLong())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                Icons.Default.AddCircle,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun OrderItemCard(
    item: OrderItemEntity,
    numberFormat: NumberFormat,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Ks ${numberFormat.format(item.totalPrice.toLong())}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${item.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (item.note.isNotBlank()) {
                Text(
                    text = "📝 ${item.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
