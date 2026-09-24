package com.printxpress.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.printxpress.app.databinding.FragmentHomeBinding
import com.printxpress.app.ui.product.CategoryProductsActivity
import com.printxpress.app.util.IntentKeys
import com.printxpress.app.util.Result

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Static for now - in a future iteration this would come from a
        // `promotions` Firestore collection, keeping Key Function 4's
        // "seasonal promotions" content editable without an app update.
        binding.textPromoTitle.text = "Seasonal Offer"
        binding.textPromoBody.text = "20% off bulk flyer orders this month"

        binding.recyclerCategories.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerCategories.adapter = CategoryAdapter(viewModel.categories) { category ->
            openCategory(category.name)
        }

        binding.inputSearch.setOnEditorActionListener { textView, _, _ ->
            val query = textView.text.toString().trim()
            if (query.isNotEmpty()) openSearch(query)
            true
        }

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadFeaturedProducts() }

        viewModel.featuredProducts.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = result is Result.Loading
            if (result is Result.Error) {
                binding.textError.text = result.message
                binding.textError.visibility = View.VISIBLE
            } else {
                binding.textError.visibility = View.GONE
            }
        }

        viewModel.loadFeaturedProducts()
    }

    private fun openCategory(category: String) {
        val intent = Intent(requireContext(), CategoryProductsActivity::class.java)
        intent.putExtra(CategoryProductsActivity.EXTRA_CATEGORY, category)
        startActivity(intent)
    }

    private fun openSearch(query: String) {
        val intent = Intent(requireContext(), CategoryProductsActivity::class.java)
        intent.putExtra(CategoryProductsActivity.EXTRA_SEARCH_QUERY, query)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
