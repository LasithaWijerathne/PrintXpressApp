package com.printxpress.app.ui.product

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.printxpress.app.data.model.Product
import com.printxpress.app.databinding.ActivityProductDetailBinding
import com.printxpress.app.ui.cart.CartActivity
import com.printxpress.app.util.IntentKeys
import com.printxpress.app.util.Result
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val viewModel: ProductDetailViewModel by viewModels()
    private var currentProduct: Product? = null

    private val filePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        val fileName = queryFileName(uri) ?: "artwork"
        val result = viewModel.onFileSelected(contentResolver, uri, fileName)
        when (result) {
            is Result.Success -> {
                binding.textSelectedFile.text = fileName
                binding.textSelectedFile.visibility = View.VISIBLE
                binding.textUploadError.visibility = View.GONE
            }
            is Result.Error -> {
                binding.textUploadError.text = result.message
                binding.textUploadError.visibility = View.VISIBLE
                binding.textSelectedFile.visibility = View.GONE
            }
            is Result.Loading -> Unit
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }
        binding.buttonUpload.setOnClickListener {
            filePicker.launch("image/*") // also accepts PDF via the system chooser's "Browse" option
        }

        val productId = intent.getStringExtra(IntentKeys.PRODUCT_ID)
        if (productId == null) {
            finish()
            return
        }

        viewModel.product.observe(this) { result ->
            when (result) {
                is Result.Success -> bindProduct(result.data)
                is Result.Error -> binding.textProductDescription.text = result.message
                is Result.Loading -> Unit
            }
        }

        viewModel.addToCartResult.observe(this) { result ->
            binding.progress.visibility = if (result is Result.Loading) View.VISIBLE else View.GONE
            binding.buttonAddToCart.isEnabled = result !is Result.Loading
            when (result) {
                is Result.Success -> startActivity(android.content.Intent(this, CartActivity::class.java))
                is Result.Error -> {
                    binding.textUploadError.text = result.message
                    binding.textUploadError.visibility = View.VISIBLE
                }
                else -> Unit
            }
        }

        viewModel.loadProduct(productId)
    }

    private fun bindProduct(product: Product) {
        currentProduct = product
        val currency = NumberFormat.getCurrencyInstance(Locale("en", "LK"))

        binding.textTitle.text = product.name
        binding.textProductName.text = product.name
        binding.textProductPrice.text = "From ${currency.format(product.basePrice)} / unit"
        binding.textProductDescription.text = product.description
        val icon = iconForProduct(product.name)
        Glide.with(this)
            .load(product.sampleImageUrl)
            .placeholder(icon)
            .error(icon)
            .centerCrop()
            .into(binding.imageSample)

        binding.spinnerPaperType.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, product.paperTypes
        )
        binding.spinnerSize.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, product.sizes
        )

        updateAddToCartLabel(product)
        binding.inputQuantity.addTextChangedListener(SimpleTextWatcher { updateAddToCartLabel(product) })

        binding.buttonAddToCart.setOnClickListener {
            val quantity = binding.inputQuantity.text.toString().toIntOrNull() ?: 0
            val paperType = binding.spinnerPaperType.selectedItem?.toString() ?: ""
            val size = binding.spinnerSize.selectedItem?.toString() ?: ""
            viewModel.addToCart(product, quantity, paperType, size)
        }
    }

    private fun updateAddToCartLabel(product: Product) {
        val quantity = binding.inputQuantity.text.toString().toIntOrNull() ?: 0
        val currency = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
        val total = product.basePrice * quantity
        binding.buttonAddToCart.text = "Add to Cart \u2014 ${currency.format(total)}"
    }

    private fun queryFileName(uri: Uri): String? {
        var name: String? = null
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) name = it.getString(index)
            }
        }
        return name
    }
}

/** Small adapter so we don't need a full TextWatcher override block inline. */
private class SimpleTextWatcher(private val onChanged: () -> Unit) : android.text.TextWatcher {
    override fun afterTextChanged(s: android.text.Editable?) = onChanged()
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}
