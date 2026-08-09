package com.beershop.pos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.beershop.pos.ui.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onTableClick = { tableId, orderId ->
                    navController.navigate(Screen.TableOrder.createRoute(tableId, orderId ?: "new"))
                },
                onTablesClick = {
                    navController.navigate(Screen.TableManagement.route)
                },
                onProductsClick = {
                    navController.navigate(Screen.ProductManagement.route)
                },
                onReportsClick = {
                    navController.navigate(Screen.Reports.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.TableManagement.route) {
            TableManagementScreen(
                onTableClick = { tableId, orderId ->
                    navController.navigate(Screen.TableOrder.createRoute(tableId, orderId ?: "new"))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TableOrder.route,
            arguments = listOf(
                navArgument(NavigationArgs.TABLE_ID) { type = NavType.StringType },
                navArgument(NavigationArgs.ORDER_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            TableOrderScreen(
                onBack = { navController.popBackStack() },
                onPayment = { orderId ->
                    navController.navigate(Screen.Payment.createRoute(orderId))
                },
                onPrint = { orderId ->
                    // Trigger print
                }
            )
        }

        composable(Screen.ProductManagement.route) {
            ProductManagementScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument(NavigationArgs.ORDER_ID) { type = NavType.StringType }
            )
        ) {
            PaymentScreen(
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Reports.route) {
            ReportsScreen(
                onReportClick = { reportType ->
                    navController.navigate(Screen.SalesReport.createRoute(reportType))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SalesReport.route,
            arguments = listOf(
                navArgument(NavigationArgs.REPORT_TYPE) { type = NavType.StringType }
            )
        ) {
            SalesReportScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onPrinterSettings = {
                    navController.navigate(Screen.PrinterSettings.route)
                },
                onUserManagement = {
                    navController.navigate(Screen.UserManagement.route)
                },
                onSyncStatus = {
                    navController.navigate(Screen.SyncStatus.route)
                }
            )
        }

        composable(Screen.PrinterSettings.route) {
            PrinterSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.UserManagement.route) {
            UserManagementScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SyncStatus.route) {
            SyncStatusScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
