package com.printxpress.app.ui.product

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.printxpress.app.data.model.OrderItem
import com.printxpress.app.data.model.Product
import com.printxpress.app.data.repository.AuthRepository
import com.printxpress.app.data.repository.ProductRepository
import com.printxpress.app.data.repository.StorageRepository
import com.printxpress.app.ui.cart.CartManager
import com.printxpress.app.util.Result
import com.printxpress.app.util.StoragePaths
import kotlinx.coroutines.launch
import java.util.UUID

class ProductDetailViewModel(
    private val productRepository: ProductRepository = ProductRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _product = MutableLiveData<Result<Product>>()
    val product: LiveData<Result<Product>> = _product

    private val _addToCartResult = MutableLiveData<Result<Unit>>()
    val addToCartResult: LiveData<Result<Unit>> = _addToCartResult

    private var selectedFileUri: Uri? = null
    private var selectedFileName: String? = null

    fun loadProduct(productId: String) {
        _product.value = Result.Loading
        viewModelScope.launch {
            _product.value = productRepository.getProduct(productId)
        }
    }

    /**
     * Validates the chosen file immediately (see StorageRepository.validateFile)
     * so a bad file is rejected before the user even presses Add to Cart,
     * rather than failing later during the upload itself.
     */
    fun onFileSelected(resolver: ContentResolver, uri: Uri, fileName: String): Result<Unit> {
        val validation = storageRepository.validateFile(resolver, uri)
        if (validation is Result.Success) {
            selectedFileUri = uri
            selectedFileName = fileName
        }
        return validation
    }

    fun addToCart(
        product: Product,
        quantity: Int,
        paperType: String,
        size: String
    ) {
        val userId = authRepository.currentUserId
        if (userId == null) {
            _addToCartResult.value = Result.Error("Please log in again.")
            return
        }
        if (quantity < 1) {
            _addToCartResult.value = Result.Error("Quantity must be at least 1.")
            return
        }

        _addToCartResult.value = Result.Loading
        viewModelScope.launch {
            var artworkUrl: String? = null

            // Uploading at "add to cart" time (rather than deferring every
            // item's upload to checkout) means a failed upload is caught
            // and reported against the specific item the user just
            // configured, while the product detail they were looking at
            // is still on screen.
            val uri = selectedFileUri
            if (uri != null) {
                val orderId = UUID.randomUUID().toString() // temp grouping id for storage path
                val path = StoragePaths.artworkPath(orderId, selectedFileName ?: "artwork")
                when (val uploadResult = storageRepository.uploadArtwork(path, uri)) {
                    is Result.Success -> artworkUrl = uploadResult.data
                    is Result.Error -> {
                        _addToCartResult.value = Result.Error(uploadResult.message)
                        return@launch
                    }
                    is Result.Loading -> Unit
                }
            }

            val item = OrderItem(
                productId = product.id,
                productName = product.name,
                quantity = quantity,
                paperType = paperType,
                size = size,
                unitPrice = product.basePrice,
                artworkUrl = artworkUrl
            )
            CartManager.addItem(item)
            _addToCartResult.value = Result.Success(Unit)
        }
    }
}
