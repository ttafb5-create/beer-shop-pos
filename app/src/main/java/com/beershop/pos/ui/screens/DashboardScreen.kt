package com.beershop.pos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.beershop.pos.data.local.entity.*
import com.beershop.pos.ui.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onTableClick: (String, String?) -> Unit,
    onTablesClick: () -> Unit,
    onProductsClick: () -> Unit,
    onReportsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ဘီယာဆိုင် POS", style = MaterialTheme.typography.titleMedium)
                        Text("Dashboard", style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onTablesClick,
                icon = { Icon(Icons.Default.TableRestaurant, contentDescription = null) },
                text = { Text("Tables / စားပွဲများ") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
            // Quick Stats Cards
            item {
                Text(
                    "Today's Summary / ယနေ့အကျဉ်းချုပ်",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        DashboardCard(
                            title = "Sales",
                            value = "Ks ${numberFormat.format(state.todaySales.toLong())}",
                            icon = Icons.Default.TrendingUp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Cash",
                            value = "Ks ${numberFormat.format(state.cashBalance.toLong())}",
                            icon = Icons.Default.Money,
                            color = Color(0xFF1565C0)
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Wallet",
                            value = "Ks ${numberFormat.format(state.walletBalance.toLong())}",
                            icon = Icons.Default.AccountBalanceWallet,
                            color = Color(0xFF6A1B9A)
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Open",
                            value = "${state.openTables}",
                            icon = Icons.Default.TableRestaurant,
                            color = Color(0xFFE65100)
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Closed",
                            value = "${state.closedTables}",
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFF4E342E)
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Orders",
                            value = "${state.totalOrders}",
                            icon = Icons.Default.ReceiptLong,
                            color = Color(0xFF00838F)
                        )
                    }
                }
            }

            // Quick Actions
            item {
                Text(
                    "Quick Actions / အမြန်လုပ်ဆောင်ရန်",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        text = "Tables\nစားပွဲများ",
                        icon = Icons.Default.TableRestaurant,
                        modifier = Modifier.weight(1f),
                        onClick = onTablesClick
                    )
                    QuickActionButton(
                        text = "Products\nပစ္စည်းများ",
                        icon = Icons.Default.Inventory2,
                        modifier = Modifier.weight(1f),
                        onClick = onProductsClick
                    )
                    QuickActionButton(
                        text = "Reports\nအစီရင်ခံစာ",
                        icon = Icons.Default.Assessment,
                        modifier = Modifier.weight(1f),
                        onClick = onReportsClick
                    )
                }
            }

            // Payment Method Breakdown
            if (state.salesBreakdown.isNotEmpty()) {
                item {
                    Text(
                        "Payment Methods / ငွေပေးချေနည်းလမ်းများ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(state.salesBreakdown) { breakdown ->
                    PaymentMethodRow(
                        method = PaymentMethod.displayName(breakdown.method),
                        amount = breakdown.total,
                        numberFormat = numberFormat
                    )
                }
            }

            // Sync Status
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Offline Mode - Data saved locally",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = color.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QuickActionButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun PaymentMethodRow(
    method: String,
    amount: Double,
    numberFormat: NumberFormat
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(method, style = MaterialTheme.typography.bodyMedium)
        Text(
            "Ks ${numberFormat.format(amount.toLong())}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
    Divider()
}
