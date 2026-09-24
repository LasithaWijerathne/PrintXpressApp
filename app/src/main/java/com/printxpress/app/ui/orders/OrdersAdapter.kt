package com.printxpress.app.ui.orders

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.printxpress.app.R
import com.printxpress.app.data.model.Order
import com.printxpress.app.data.model.OrderStatus
import com.printxpress.app.databinding.ItemOrderBinding

class OrdersAdapter(
    private var orders: List<Order>,
    private val onClick: (Order) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        val context = holder.itemView.context
        val status = OrderStatus.fromString(order.status)

        holder.binding.textOrderId.text = "#${order.id.take(6).uppercase()}"
        holder.binding.textOrderSummary.text = order.items.joinToString(", ") {
            "${it.productName} x${it.quantity}"
        }
        holder.binding.textOrderFooter.text =
            if (status == OrderStatus.CANCELLED) "Cancelled" else "Tap for details \u2192"

        holder.binding.textStatus.text = statusLabel(status)
        val colorRes = when (status) {
            OrderStatus.PROCESSING -> R.color.status_processing
            OrderStatus.PRINTING -> R.color.status_printing
            OrderStatus.READY_FOR_PICKUP -> R.color.status_ready
            OrderStatus.CANCELLED -> R.color.status_cancelled
        }
        holder.binding.textStatus.backgroundTintList =
            ColorStateList.valueOf(context.getColor(colorRes))

        holder.binding.root.setOnClickListener { onClick(order) }
    }

    override fun getItemCount() = orders.size

    fun submitList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    private fun statusLabel(status: OrderStatus) = when (status) {
        OrderStatus.PROCESSING -> "Processing"
        OrderStatus.PRINTING -> "Printing"
        OrderStatus.READY_FOR_PICKUP -> "Ready for pickup"
        OrderStatus.CANCELLED -> "Cancelled"
    }
}
