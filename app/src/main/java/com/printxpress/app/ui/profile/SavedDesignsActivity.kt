package com.printxpress.app.ui.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.printxpress.app.data.model.SavedDesign
import com.printxpress.app.data.repository.AuthRepository
import com.printxpress.app.data.repository.UserRepository
import com.printxpress.app.databinding.ActivitySavedDesignsBinding
import com.printxpress.app.util.Result
import kotlinx.coroutines.launch

class SavedDesignsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedDesignsBinding
    private val userRepository = UserRepository()
    private val authRepository = AuthRepository()
    private lateinit var adapter: SavedDesignAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedDesignsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }

        adapter = SavedDesignAdapter(emptyList()) { design -> deleteDesign(design) }
        binding.recyclerDesigns.layoutManager = LinearLayoutManager(this)
        binding.recyclerDesigns.adapter = adapter

        loadDesigns()
    }

    private fun loadDesigns() {
        val uid = authRepository.currentUserId ?: return
        lifecycleScope.launch {
            val result = userRepository.getSavedDesigns(uid)
            if (result is Result.Success) {
                adapter.submitList(result.data)
                binding.textEmpty.visibility = if (result.data.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerDesigns.visibility = if (result.data.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun deleteDesign(design: SavedDesign) {
        val uid = authRepository.currentUserId ?: return
        lifecycleScope.launch {
            userRepository.deleteSavedDesign(uid, design.id)
            loadDesigns()
        }
    }
}
