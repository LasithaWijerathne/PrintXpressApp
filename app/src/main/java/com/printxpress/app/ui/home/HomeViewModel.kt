package com.printxpress.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.printxpress.app.R
import com.printxpress.app.data.model.Product
import com.printxpress.app.data.repository.ProductRepository
import com.printxpress.app.util.Result
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productRepository: ProductRepository = ProductRepository()
) : ViewModel() {

    /** Fixed category list shown on Home; each maps to Product.category values in Firestore. */
    val categories = listOf(
        CategoryItem("Business Cards", R.drawable.ic_cat_cards),
        CategoryItem("Flyers", R.drawable.ic_cat_flyers),
        CategoryItem("Banners", R.drawable.ic_cat_banners),
        CategoryItem("Stickers", R.drawable.ic_cat_stickers),
        CategoryItem("T-Shirts", R.drawable.ic_cat_tshirt),
        CategoryItem("Mugs", R.drawable.ic_cat_mug),
    )

    private val _featuredProducts = MutableLiveData<Result<List<Product>>>()
    val featuredProducts: LiveData<Result<List<Product>>> = _featuredProducts

    fun loadFeaturedProducts() {
        _featuredProducts.value = Result.Loading
        viewModelScope.launch {
            _featuredProducts.value = productRepository.getAllProducts()
        }
    }
}
