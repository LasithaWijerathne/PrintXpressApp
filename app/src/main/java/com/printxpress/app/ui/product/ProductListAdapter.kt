package com.printxpress.app.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.printxpress.app.R
import com.printxpress.app.data.model.Product
import com.printxpress.app.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale

class ProductListAdapter(
    private var products: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        val currency = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
        holder.binding.textProductName.text = product.name
        holder.binding.textProductPrice.text = "From ${currency.format(product.basePrice)} / unit"

        // Uses a local icon matching the product name as both the placeholder
        // (shown instantly) and the fallback if the remote sampleImageUrl
        // fails or is empty - the product always looks complete either way.
        val icon = iconForProduct(product.name)
        Glide.with(holder.binding.imageProduct)
            .load(product.sampleImageUrl)
            .placeholder(icon)
            .error(icon)
            .centerCrop()
            .into(holder.binding.imageProduct)
        holder.binding.root.setOnClickListener { onClick(product) }
    }

    override fun getItemCount() = products.size

    fun submitList(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}

/** Maps a product name to a locally-bundled icon, so every product looks
 *  finished even before/without a real photo. Falls back to the generic
 *  standard-card icon for any name not specifically matched. */
fun iconForProduct(name: String): Int = when {
    name.contains("Double-Sided", ignoreCase = true) -> R.drawable.ic_prod_doublesided
    name.contains("Foil", ignoreCase = true) -> R.drawable.ic_prod_foil
    name.contains("Spot UV", ignoreCase = true) -> R.drawable.ic_prod_spotuv
    name.contains("Rounded", ignoreCase = true) -> R.drawable.ic_prod_rounded
    name.contains("Textured", ignoreCase = true) -> R.drawable.ic_prod_textured
    name.contains("Trifold", ignoreCase = true) -> R.drawable.ic_prod_trifold
    name.contains("DL", ignoreCase = false) -> R.drawable.ic_prod_flyer_dl
    name.contains("A4", ignoreCase = false) -> R.drawable.ic_prod_flyer_a4
    name.contains("A5", ignoreCase = false) -> R.drawable.ic_prod_flyer_a5
    name.contains("Roll-Up", ignoreCase = true) -> R.drawable.ic_prod_rollup
    name.contains("PVC", ignoreCase = true) -> R.drawable.ic_prod_pvcbanner
    name.contains("Mesh", ignoreCase = true) -> R.drawable.ic_prod_mesh
    name.contains("Teardrop", ignoreCase = true) -> R.drawable.ic_prod_teardrop
    name.contains("Wall Banner", ignoreCase = true) -> R.drawable.ic_prod_wallbanner
    name.contains("Sticker Sheet", ignoreCase = true) || name.contains("Label", ignoreCase = true) -> R.drawable.ic_prod_sticker_sheet
    name.contains("Die-Cut", ignoreCase = true) || name.contains("Transparent", ignoreCase = true) -> R.drawable.ic_prod_sticker_diecut
    name.contains("Bumper", ignoreCase = true) -> R.drawable.ic_prod_sticker_bumper
    name.contains("Polo", ignoreCase = true) -> R.drawable.ic_prod_tshirt_polo
    name.contains("Tee", ignoreCase = true) || name.contains("Shirt", ignoreCase = true) -> R.drawable.ic_prod_tshirt_basic
    name.contains("Magic", ignoreCase = true) -> R.drawable.ic_prod_mug_magic
    name.contains("Travel Mug", ignoreCase = true) -> R.drawable.ic_prod_mug_travel
    name.contains("Mug", ignoreCase = true) -> R.drawable.ic_prod_mug_classic
    else -> R.drawable.ic_prod_standard
}
