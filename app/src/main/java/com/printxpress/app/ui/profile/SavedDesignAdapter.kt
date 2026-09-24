package com.printxpress.app.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.printxpress.app.data.model.SavedDesign
import com.printxpress.app.databinding.ItemAddressBinding
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Reuses item_address.xml's plain "title / subtitle / delete button"
 * layout rather than a near-identical new one, since a saved design and
 * a saved address are presented the same way in this app: a label, a
 * detail line, and a delete action.
 */
class SavedDesignAdapter(
    private var designs: List<SavedDesign>,
    private val onDelete: (SavedDesign) -> Unit
) : RecyclerView.Adapter<SavedDesignAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val design = designs[position]
        val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.binding.textLabel.text = design.fileName
        holder.binding.textAddressLine.text =
            design.createdAt?.toDate()?.let { "Saved ${format.format(it)}" } ?: "Saved design"
        holder.binding.buttonDelete.setOnClickListener { onDelete(design) }
    }

    override fun getItemCount() = designs.size

    fun submitList(newList: List<SavedDesign>) {
        designs = newList
        notifyDataSetChanged()
    }
}
