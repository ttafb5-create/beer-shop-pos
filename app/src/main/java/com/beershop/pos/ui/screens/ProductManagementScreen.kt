package com.beershop.pos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.beershop.pos.data.local.entity.*
import com.beershop.pos.ui.viewmodel.ProductManagementViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    onBack: () -> Unit,
    viewModel: ProductManagementViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val products = state.products
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Filter locally
    val filteredProducts = remember(products, selectedCategory, searchQuery) {
        var result = products
        if (selectedCategory != null) {
            result = result.filter { it.category == selectedCategory }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.lowercase()
            result = result.filter {
                it.name.lowercase().contains(q) ||
                it.nameMyanmar.contains(q) ||
                (it.barcode?.contains(q) == true)
            }
        }
        result
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products / \u1015\u1005\u1039\u1005\u100a\u103a\u1038\u1019\u103b\u102c\u1038") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Product")
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
            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotBlank()) viewModel.searchProducts(it)
                    else viewModel.loadProducts()
                },
                label = { Text("Search / \u101b\u103e\u102c\u101b\u1014\u103a") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                singleLine = true
            )

            // Categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All") }
                )
                ProductCategory.ALL.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text("${ProductCategory.emoji(cat)} ${ProductCategory.displayName(cat)}", fontSize = 12.sp) }
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            // Product Grid
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No products yet\n\u1015\u1005\u1039\u1005\u100a\u103a\u1038\u1019\u101b\u103e\u102d\u101e\u1038",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Product")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            numberFormat = numberFormat,
                            onClick = { /* edit product */ }
                        )
                    }
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, nameMm, cat, sellPrice, costPrice, stock, unit, barcode, tax ->
                viewModel.addProduct(
                    name = name,
                    nameMyanmar = nameMm,
                    category = cat,
                    sellingPrice = sellPrice,
                    costPrice = costPrice,
                    stock = stock,
                    unit = unit,
                    barcode = barcode,
                    taxRate = tax
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    numberFormat: NumberFormat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("${ProductCategory.emoji(product.category)}", fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                product.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (product.nameMyanmar.isNotBlank()) {
                Text(
                    product.nameMyanmar,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Ks ${numberFormat.format(product.sellingPrice.toLong())}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Stock: ${product.stockQuantity} ${product.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = if (product.stockQuantity <= 0)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Double, Double, Double, String, String?, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var nameMyanmar by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ProductCategory.BEER) }
    var sellingPrice by remember { mutableStateOf("") }
    var costPrice by remember { mutableStateOf("") }
    var stockQuantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("\u1018\u1030\u1038") }
    var barcode by remember { mutableStateOf("") }
    var taxRate by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Product / \u1015\u1005\u1039\u1005\u100a\u103a\u1038\u1021\u101e\u1005\u103a") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = nameMyanmar,
                    onValueChange = { nameMyanmar = it },
                    label = { Text("Myanmar Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text("Category", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ProductCategory.ALL.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(ProductCategory.emoji(cat), fontSize = 14.sp) }
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sellingPrice,
                        onValueChange = { sellingPrice = it },
                        label = { Text("Selling Price *") },
                        leadingIcon = { Text("Ks") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = costPrice,
                        onValueChange = { costPrice = it },
                        label = { Text("Cost Price") },
                        leadingIcon = { Text("Ks") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stockQuantity,
                        onValueChange = { stockQuantity = it },
                        label = { Text("Stock Qty") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Barcode (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = taxRate,
                    onValueChange = { taxRate = it },
                    label = { Text("Tax Rate %") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { Text("%") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = sellingPrice.toDoubleOrNull() ?: 0.0
                    val cost = costPrice.toDoubleOrNull() ?: 0.0
                    val stock = stockQuantity.toDoubleOrNull() ?: 0.0
                    val tax = taxRate.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && price > 0) {
                        onSave(name, nameMyanmar, category, price, cost, stock, unit, barcode.ifBlank { null }, tax)
                    }
                },
                enabled = name.isNotBlank() && (sellingPrice.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Save / \u101e\u102d\u1019\u103a\u1038\u1019\u100a\u103a")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


