package com.printxpress.app.ui.checkout

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.printxpress.app.data.model.Address
import com.printxpress.app.ui.cart.CartManager
import com.printxpress.app.ui.checkout.OrderConfirmationActivity
import com.printxpress.app.databinding.ActivityCheckoutBinding
import com.printxpress.app.util.Result
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private val viewModel: CheckoutViewModel by viewModels()

    private var fulfilment = "pickup"
    private var addressList: List<Address> = emptyList()
    private var selectedAddress: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }

        updateTotalDisplay()

        binding.buttonPickup.setOnClickListener { setFulfilment("pickup") }
        binding.buttonDelivery.setOnClickListener { setFulfilment("delivery") }
        setFulfilment("pickup")

        binding.inputDate.setOnClickListener { showDatePicker() }

        binding.spinnerAddress.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedAddress = addressList.getOrNull(position)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })

        binding.buttonPlaceOrder.setOnClickListener {
            val date = binding.inputDate.text.toString()
            viewModel.placeOrder(fulfilment, if (fulfilment == "delivery") selectedAddress else null, date)
        }

        viewModel.addresses.observe(this) { result ->
            if (result is Result.Success) {
                addressList = result.data
                binding.spinnerAddress.adapter = ArrayAdapter(
                    this, android.R.layout.simple_spinner_dropdown_item,
                    result.data.map { "${it.label} \u2014 ${it.line1}, ${it.city}" }
                )
                selectedAddress = result.data.firstOrNull { it.isDefault } ?: result.data.firstOrNull()
            }
        }

        viewModel.placeOrderResult.observe(this) { result ->
            binding.progress.visibility = if (result is Result.Loading) View.VISIBLE else View.GONE
            binding.buttonPlaceOrder.isEnabled = result !is Result.Loading
            when (result) {
                is Result.Success -> {
                    CartManager.clear()
                    startActivity(
                        Intent(this, OrderConfirmationActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    finish()
                }
                is Result.Error -> {
                    binding.textError.text = result.message
                    binding.textError.visibility = View.VISIBLE
                }
                else -> Unit
            }
        }

        viewModel.loadAddresses()
    }

    private fun setFulfilment(value: String) {
        fulfilment = value
        val isDelivery = value == "delivery"
        binding.labelAddress.visibility = if (isDelivery) View.VISIBLE else View.GONE
        binding.spinnerAddress.visibility = if (isDelivery) View.VISIBLE else View.GONE
        binding.buttonPickup.isChecked = !isDelivery
        binding.buttonDelivery.isChecked = isDelivery
        updateTotalDisplay()
    }

    private fun updateTotalDisplay() {
        val currency = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
        val total = CartManager.subtotal() + CartManager.deliveryFee(fulfilment)
        binding.textTotal.text = currency.format(total)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val format = java.text.SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                binding.inputDate.setText(format.format(calendar.time))
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() // can't schedule a date in the past
        }.show()
    }
}
