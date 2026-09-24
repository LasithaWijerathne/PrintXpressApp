package com.printxpress.app.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.printxpress.app.data.model.Address
import com.printxpress.app.data.repository.AuthRepository
import com.printxpress.app.data.repository.UserRepository
import com.printxpress.app.databinding.ActivityAddressesBinding
import com.printxpress.app.util.Result
import kotlinx.coroutines.launch

class AddressesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressesBinding
    private val userRepository = UserRepository()
    private val authRepository = AuthRepository()
    private lateinit var adapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }

        adapter = AddressAdapter(emptyList()) { address -> deleteAddress(address) }
        binding.recyclerAddresses.layoutManager = LinearLayoutManager(this)
        binding.recyclerAddresses.adapter = adapter

        binding.buttonAddAddress.setOnClickListener { addAddress() }

        loadAddresses()
    }

    private fun loadAddresses() {
        val uid = authRepository.currentUserId ?: return
        lifecycleScope.launch {
            val result = userRepository.getAddresses(uid)
            if (result is Result.Success) adapter.submitList(result.data)
        }
    }

    private fun addAddress() {
        val uid = authRepository.currentUserId ?: return
        val label = binding.inputLabel.text.toString().trim()
        val line1 = binding.inputLine1.text.toString().trim()
        val city = binding.inputCity.text.toString().trim()
        if (label.isEmpty() || line1.isEmpty() || city.isEmpty()) return

        lifecycleScope.launch {
            val result = userRepository.addAddress(uid, Address(label = label, line1 = line1, city = city))
            if (result is Result.Success) {
                binding.inputLabel.text?.clear()
                binding.inputLine1.text?.clear()
                binding.inputCity.text?.clear()
                loadAddresses()
            }
        }
    }

    private fun deleteAddress(address: Address) {
        val uid = authRepository.currentUserId ?: return
        lifecycleScope.launch {
            userRepository.deleteAddress(uid, address.id)
            loadAddresses()
        }
    }
}
