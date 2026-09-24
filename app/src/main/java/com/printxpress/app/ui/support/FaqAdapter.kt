package com.printxpress.app.ui.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.printxpress.app.databinding.ItemFaqBinding

data class FaqItem(val question: String, val answer: String, var expanded: Boolean = false)

class FaqAdapter(private val items: List<FaqItem>) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textQuestion.text = item.question
        holder.binding.textAnswer.text = item.answer
        holder.binding.textAnswer.visibility = if (item.expanded) View.VISIBLE else View.GONE
        holder.binding.textToggle.text = if (item.expanded) "\u2212" else "+"

        holder.binding.root.setOnClickListener {
            item.expanded = !item.expanded
            notifyItemChanged(holder.adapterPosition)
        }
    }

    override fun getItemCount() = items.size
}
