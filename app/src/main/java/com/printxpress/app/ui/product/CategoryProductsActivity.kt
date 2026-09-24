package com.printxpress.app.ui.product

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.printxpress.app.data.repository.ProductRepository
import com.printxpress.app.databinding.ActivityCategoryProductsBinding
import com.printxpress.app.util.IntentKeys
import com.printxpress.app.util.Result
import kotlinx.coroutines.launch


class CategoryProductsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_SEARCH_QUERY = "extra_search_query"
    }

    private lateinit var binding: ActivityCategoryProductsBinding
    private val productRepository = ProductRepository()
    private lateinit var adapter: ProductListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }

        adapter = ProductListAdapter(emptyList()) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra(IntentKeys.PRODUCT_ID, product.id)
            startActivity(intent)
        }
        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = adapter

        val category = intent.getStringExtra(EXTRA_CATEGORY)
        val searchQuery = intent.getStringExtra(EXTRA_SEARCH_QUERY)
        binding.textTitle.text = category ?: "Search: $searchQuery"

        binding.swipeRefresh.setOnRefreshListener { load(category, searchQuery) }
        load(category, searchQuery)
    }

    private fun load(category: String?, searchQuery: String?) {
        binding.swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            val result = if (category != null) {
                productRepository.getProductsByCategory(category)
            } else {
                // Search has no dedicated query - fetch everything once,
                // then filter client-side (see ProductRepository.filterByQuery
                // for why this is reasonable at this catalogue's size).
                val all = productRepository.getAllProducts()
                if (all is Result.Success) {
                    Result.Success(productRepository.filterByQuery(all.data, searchQuery ?: ""))
                } else all
            }

            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Result.Success -> {
                    adapter.submitList(result.data)
                    binding.textEmpty.visibility = if (result.data.isEmpty()) View.VISIBLE else View.GONE
                }
                is Result.Error -> binding.textEmpty.apply {
                    text = result.message
                    visibility = View.VISIBLE
                }
                is Result.Loading -> Unit
            }
        }
    }
}
