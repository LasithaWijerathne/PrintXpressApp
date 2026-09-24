package com.printxpress.app.ui.orders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.printxpress.app.databinding.FragmentOrdersBinding
import com.printxpress.app.util.IntentKeys
import com.printxpress.app.util.Result

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var adapter: OrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OrdersAdapter(emptyList()) { order ->
            val intent = Intent(requireContext(), OrderDetailActivity::class.java)
            intent.putExtra(IntentKeys.ORDER_ID, order.id)
            startActivity(intent)
        }
        binding.recyclerOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOrders.adapter = adapter

        viewModel.orders.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    adapter.submitList(result.data)
                    binding.textEmpty.visibility = if (result.data.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerOrders.visibility = if (result.data.isEmpty()) View.GONE else View.VISIBLE
                }
                is Result.Error -> {
                    binding.textEmpty.text = result.message
                    binding.textEmpty.visibility = View.VISIBLE
                }
                is Result.Loading -> Unit
            }
        }

        viewModel.startObservingOrders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
