package com.printxpress.app.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.printxpress.app.databinding.ActivityCartBinding
import com.printxpress.app.ui.checkout.CheckoutActivity
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }

        adapter = CartAdapter(emptyList()) { position -> CartManager.removeItem(position) }
        binding.recyclerCart.layoutManager = LinearLayoutManager(this)
        binding.recyclerCart.adapter = adapter

        binding.buttonCheckout.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }

        CartManager.items.observe(this) { items ->
            adapter.submitList(items)
            binding.textTitle.text = "Cart (${items.size} item${if (items.size == 1) "" else "s"})"
            binding.textEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerCart.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
            binding.buttonCheckout.isEnabled = items.isNotEmpty()

            val currency = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
            binding.textSubtotal.text = currency.format(CartManager.subtotal())
            binding.textTotal.text = currency.format(CartManager.subtotal())
        }
    }
}
