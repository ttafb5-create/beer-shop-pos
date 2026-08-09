package com.beershop.pos.printer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.beershop.pos.data.local.entity.*
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThermalPrinterManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var selectedPrinter: BluetoothDevice? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)

    fun getPairedPrinters(): List<BluetoothDevice> {
        return try {
            val printers = BluetoothPrintersConnections.selectFirstPaired()
            val adapter = BluetoothAdapter.getDefaultAdapter()
            adapter?.bondedDevices?.filter {
                it.name.lowercase().contains("printer") ||
                it.name.lowercase().contains("pos") ||
                it.name.lowercase().contains("thermal") ||
                it.name.lowercase().contains("58") ||
                it.name.lowercase().contains("80")
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun connectToPrinter(address: String): Boolean {
        return try {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            selectedPrinter = adapter?.getRemoteDevice(address)
            selectedPrinter != null
        } catch (e: Exception) {
            false
        }
    }

    fun disconnect() {
        selectedPrinter = null
    }

    fun isConnected(): Boolean = selectedPrinter != null

    fun printReceipt(
        order: OrderEntity,
        items: List<OrderItemEntity>,
        payments: List<PaymentEntity>,
        table: TableEntity?,
        shopName: String = "Beer Shop",
        shopAddress: String = "",
        shopPhone: String = "",
        paperWidth: Int = 58
    ): Boolean {
        val printer = selectedPrinter ?: return false

        return try {
            val connection = BluetoothConnection(printer)
            val escPosPrinter = EscPosPrinter(connection, 203, paperWidth.toFloat(), 32)

            val receipt = buildReceiptText(
                order = order,
                items = items,
                payments = payments,
                table = table,
                shopName = shopName,
                shopAddress = shopAddress,
                shopPhone = shopPhone
            )

            escPosPrinter.printFormattedTextAndCut(receipt)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun buildReceiptText(
        order: OrderEntity,
        items: List<OrderItemEntity>,
        payments: List<PaymentEntity>,
        table: TableEntity?,
        shopName: String,
        shopAddress: String,
        shopPhone: String
    ): String {
        val now = Date()
        val sb = StringBuilder()

        // Header
        sb.appendLine("[C]<b><font size='big'>$shopName</font></b>")
        if (shopAddress.isNotBlank()) {
            sb.appendLine("[C]<font size='normal'>$shopAddress</font>")
        }
        if (shopPhone.isNotBlank()) {
            sb.appendLine("[C]<font size='normal'>Tel: $shopPhone</font>")
        }
        sb.appendLine("[C]--------------------------------")

        // Order Info
        sb.appendLine("[L]Date: ${dateFormat.format(now)}")
        sb.appendLine("[L]Order: ${order.orderNumber}")
        table?.let { sb.appendLine("[L]Table: ${it.tableNumber} ${it.tableName}") }
        sb.appendLine("[L]Cashier: ${order.cashierName}")
        sb.appendLine("[C]--------------------------------")

        // Items Header
        sb.appendLine("[L]<b>Item          Qty  Price   Amount</b>")
        sb.appendLine("[C]--------------------------------")

        // Items
        for (item in items) {
            val name = if (item.productNameMyanmar.isNotBlank())
                item.productNameMyanmar.take(12)
            else
                item.productName.take(12)

            val qty = item.quantity.toString().padStart(3)
            val price = formatPrice(item.unitPrice.toLong()).padStart(6)
            val amount = formatPrice(item.totalPrice.toLong()).padStart(8)

            sb.appendLine("[L]$name  $qty  $price  $amount")

            if (item.note.isNotBlank()) {
                sb.appendLine("[L]  * ${item.note.take(20)}")
            }
        }

        sb.appendLine("[C]--------------------------------")

        // Totals
        val subtotal = formatPrice(order.subtotal.toLong())
        sb.appendLine("[R]Subtotal:        $subtotal")

        if (order.discountAmount > 0) {
            val discount = formatPrice(order.discountAmount.toLong())
            sb.appendLine("[R]Discount:       -$discount")
            if (order.discountPercent > 0) {
                sb.appendLine("[R]  (${order.discountPercent.toInt()}%)")
            }
        }

        if (order.serviceCharge > 0) {
            val sc = formatPrice(order.serviceCharge.toLong())
            sb.appendLine("[R]Service Charge:  $sc")
        }

        if (order.taxAmount > 0) {
            val tax = formatPrice(order.taxAmount.toLong())
            sb.appendLine("[R]Tax:             $tax")
        }

        sb.appendLine("[C]--------------------------------")
        val total = formatPrice(order.grandTotal.toLong())
        sb.appendLine("[R]<b><font size='big'>TOTAL:    $total</font></b>")
        sb.appendLine("[C]--------------------------------")

        // Payment Info
        if (payments.isNotEmpty()) {
            sb.appendLine("[L]<b>Payment:</b>")
            for (payment in payments) {
                val method = PaymentMethod.displayName(payment.method).take(15)
                val amount = formatPrice(payment.amount.toLong())
                sb.appendLine("[L]  $method: $amount")
            }
            sb.appendLine("[C]--------------------------------")
        }

        // Footer
        sb.appendLine("[C]<font size='normal'>Thank You!</font>")
        sb.appendLine("[C]<font size='normal'>ကျေးဇူးတင်ပါတယ်</font>")
        sb.appendLine("[C]")

        // Cut paper
        sb.appendLine("[C]")
        sb.appendLine("[C]")
        sb.appendLine("[C]")

        return sb.toString()
    }

    fun printKitchenOrder(
        order: OrderEntity,
        items: List<OrderItemEntity>,
        table: TableEntity?
    ): Boolean {
        val printer = selectedPrinter ?: return false

        return try {
            val connection = BluetoothConnection(printer)
            val escPosPrinter = EscPosPrinter(connection, 203, 58f, 32)

            val sb = StringBuilder()
            sb.appendLine("[C]<b><font size='big'>KITCHEN ORDER</font></b>")
            sb.appendLine("[C]--------------------------------")
            sb.appendLine("[L]Order: ${order.orderNumber}")
            table?.let { sb.appendLine("[L]Table: ${it.tableNumber}") }
            sb.appendLine("[L]Time: ${dateFormat.format(Date())}")
            sb.appendLine("[C]================================\n")

            for (item in items) {
                sb.appendLine("[L]<b><font size='big'>${item.quantity}x ${item.productName}</font></b>")
                if (item.note.isNotBlank()) {
                    sb.appendLine("[L]  Note: ${item.note}")
                }
                sb.appendLine("[C]--------------------------------")
            }

            escPosPrinter.printFormattedTextAndCut(sb.toString())
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun formatPrice(price: Long): String {
        return "Ks ${"%,d".format(price)}"
    }
}
