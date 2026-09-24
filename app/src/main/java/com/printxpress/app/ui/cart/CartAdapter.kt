package com.printxpress.app.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.printxpress.app.data.model.OrderItem
import com.printxpress.app.databinding.ItemCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private var items: List<OrderItem>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val currency = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
        holder.binding.textItemName.text = "${item.productName} x${item.quantity} (${item.paperType})"
        holder.binding.textItemPrice.text = currency.format(item.lineTotal)
        holder.binding.buttonRemove.setOnClickListener { onRemove(holder.adapterPosition) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<OrderItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
