package com.printxpress.app.ui.orders

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.printxpress.app.data.model.Order
import com.printxpress.app.data.model.OrderStatus
import com.printxpress.app.databinding.ActivityOrderDetailBinding
import com.printxpress.app.util.IntentKeys
import com.printxpress.app.util.Result
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private val viewModel: OrderDetailViewModel by viewModels()
    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }

        orderId = intent.getStringExtra(IntentKeys.ORDER_ID)
        if (orderId == null) {
            finish()
            return
        }

        binding.buttonCancel.setOnClickListener { confirmCancel() }
        binding.buttonReschedule.setOnClickListener { showDatePicker() }

        viewModel.order.observe(this) { result ->
            if (result is Result.Success) bindOrder(result.data)
        }

        viewModel.actionResult.observe(this) { result ->
            binding.progress.visibility = if (result is Result.Loading) View.VISIBLE else View.GONE
            if (result is Result.Error) {
                AlertDialog.Builder(this).setMessage(result.message).setPositiveButton("OK", null).show()
            }
        }

        viewModel.loadOrder(orderId!!)
    }

    private fun bindOrder(order: Order) {
        val currency = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
        val status = OrderStatus.fromString(order.status)

        binding.textOrderId.text = "#${order.id.take(6).uppercase()}"
        binding.textStatus.text = status.name.replace("_", " ").lowercase()
            .replaceFirstChar { it.uppercase() }
        binding.textScheduledDate.text =
            "Scheduled: ${order.scheduledDate} \u2022 ${order.fulfilment.replaceFirstChar { it.uppercase() }}"
        binding.textTotal.text = currency.format(order.totalPrice)

        binding.containerItems.removeAllViews()
        order.items.forEach { item ->
            val row = TextView(this)
            row.text = "${item.productName} x${item.quantity} (${item.paperType}, ${item.size}) \u2014 ${currency.format(item.lineTotal)}"
            row.setTextColor(getColor(com.printxpress.app.R.color.text_secondary))
            row.textSize = 13f
            row.setPadding(0, 4, 0, 4)
            binding.containerItems.addView(row)
        }

        val canModify = order.canCancel()
        binding.buttonReschedule.visibility = if (canModify) View.VISIBLE else View.GONE
        binding.buttonCancel.visibility = if (canModify) View.VISIBLE else View.GONE
        binding.textLockedNotice.visibility = if (canModify) View.GONE else View.VISIBLE
        if (!canModify) {
            binding.textLockedNotice.text = if (status == OrderStatus.CANCELLED) {
                "This order has been cancelled."
            } else {
                "This order is already printing and can no longer be changed."
            }
        }
    }

    private fun confirmCancel() {
        AlertDialog.Builder(this)
            .setTitle("Cancel this order?")
            .setMessage("This can't be undone.")
            .setPositiveButton("Cancel order") { _, _ -> orderId?.let { viewModel.cancelOrder(it) } }
            .setNegativeButton("Keep order", null)
            .show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val format = java.text.SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                orderId?.let { viewModel.rescheduleOrder(it, format.format(calendar.time)) }
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }
}
