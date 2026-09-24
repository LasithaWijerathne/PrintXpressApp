package com.printxpress.app.ui.checkout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.printxpress.app.databinding.ActivityOrderConfirmationBinding
import com.printxpress.app.ui.main.MainActivity


class OrderConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textOrderId.text =
            "Your order has been sent to our team. You'll get a notification the moment it's confirmed."

        binding.buttonViewOrders.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        }
    }
}
