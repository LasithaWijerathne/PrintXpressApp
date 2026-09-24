package com.printxpress.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.printxpress.app.data.repository.AuthRepository
import com.printxpress.app.data.repository.UserRepository
import com.printxpress.app.databinding.FragmentProfileBinding
import com.printxpress.app.ui.auth.LoginActivity
import com.printxpress.app.ui.support.GuidelinesFaqActivity
import com.printxpress.app.util.Result
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rowSavedDesigns.setOnClickListener {
            startActivity(Intent(requireContext(), SavedDesignsActivity::class.java))
        }
        binding.rowAddresses.setOnClickListener {
            startActivity(Intent(requireContext(), AddressesActivity::class.java))
        }
        binding.rowOrderHistory.setOnClickListener {
            // Order history is the same real-time list shown on the Orders
            // tab (a Firestore query with no date cut-off) - rather than a
            // separate screen and a second, near-duplicate query, this row
            // just switches to that tab on the host Activity's bottom nav.
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                com.printxpress.app.R.id.bottom_nav
            ).selectedItemId = com.printxpress.app.R.id.nav_orders
        }
        binding.rowGuidelines.setOnClickListener {
            startActivity(Intent(requireContext(), GuidelinesFaqActivity::class.java))
        }
        binding.rowLogout.setOnClickListener {
            authRepository.logout()
            startActivity(
                Intent(requireContext(), LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        loadProfile()
    }

    private fun loadProfile() {
        val uid = authRepository.currentUserId ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            val result = userRepository.getUser(uid)
            if (result is Result.Success) {
                binding.textName.text = result.data.name
                binding.textEmail.text = result.data.email
                binding.textAvatarInitial.text = result.data.name.firstOrNull()?.uppercase() ?: "?"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
