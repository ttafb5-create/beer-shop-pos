package com.beershop.pos.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object TableManagement : Screen("tables")
    object TableOrder : Screen("table_order/{tableId}/{orderId}") {
        fun createRoute(tableId: String, orderId: String = "new") =
            "table_order/$tableId/$orderId"
    }
    object ProductManagement : Screen("products")
    // ProductForm removed - using dialog instead
    object Payment : Screen("payment/{orderId}") {
        fun createRoute(orderId: String) = "payment/$orderId"
    }
    object Reports : Screen("reports")
    object SalesReport : Screen("sales_report/{reportType}") {
        fun createRoute(reportType: String) = "sales_report/$reportType"
    }
    object Settings : Screen("settings")
    object PrinterSettings : Screen("printer_settings")
    object UserManagement : Screen("user_management")
    object SyncStatus : Screen("sync_status")
}

object NavigationArgs {
    const val TABLE_ID = "tableId"
    const val ORDER_ID = "orderId"
    const val REPORT_TYPE = "reportType"
}
