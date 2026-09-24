package com.printxpress.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.printxpress.app.databinding.ItemCategoryBinding

data class CategoryItem(val name: String, @DrawableRes val iconRes: Int)

class CategoryAdapter(
    private val categories: List<CategoryItem>,
    private val onClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.textCategoryName.text = category.name
        holder.binding.imageCategoryIcon.setImageResource(category.iconRes)
        holder.binding.root.setOnClickListener { onClick(category) }
    }

    override fun getItemCount() = categories.size
}
