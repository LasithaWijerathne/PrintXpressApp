package com.printxpress.app.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.printxpress.app.databinding.FragmentNotificationsBinding
import com.printxpress.app.util.Result

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsViewModel by viewModels()
    private lateinit var adapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NotificationsAdapter(emptyList()) { notification ->
            viewModel.markAsRead(notification)
        }
        binding.recyclerNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNotifications.adapter = adapter

        viewModel.notifications.observe(viewLifecycleOwner) { result ->
            if (result is Result.Success) {
                adapter.submitList(result.data)
                binding.textEmpty.visibility = if (result.data.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerNotifications.visibility = if (result.data.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        viewModel.startObserving()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
