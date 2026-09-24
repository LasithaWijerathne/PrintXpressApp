package com.printxpress.app.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.printxpress.app.data.model.Address
import com.printxpress.app.databinding.ItemAddressBinding

class AddressAdapter(
    private var addresses: List<Address>,
    private val onDelete: (Address) -> Unit
) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addresses[position]
        holder.binding.textLabel.text = address.label
        holder.binding.textAddressLine.text = "${address.line1}, ${address.city}"
        holder.binding.buttonDelete.setOnClickListener { onDelete(address) }
    }

    override fun getItemCount() = addresses.size

    fun submitList(newList: List<Address>) {
        addresses = newList
        notifyDataSetChanged()
    }
}
