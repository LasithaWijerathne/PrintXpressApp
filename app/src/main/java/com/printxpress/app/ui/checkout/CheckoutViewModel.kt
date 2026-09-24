package com.printxpress.app.ui.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.printxpress.app.data.model.Address
import com.printxpress.app.data.model.Order
import com.printxpress.app.data.repository.AuthRepository
import com.printxpress.app.data.repository.OrderRepository
import com.printxpress.app.data.repository.UserRepository
import com.printxpress.app.ui.cart.CartManager
import com.printxpress.app.util.Result
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _addresses = MutableLiveData<Result<List<Address>>>()
    val addresses: LiveData<Result<List<Address>>> = _addresses

    private val _placeOrderResult = MutableLiveData<Result<String>>()
    val placeOrderResult: LiveData<Result<String>> = _placeOrderResult

    fun loadAddresses() {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _addresses.value = userRepository.getAddresses(userId)
        }
    }


    fun placeOrder(fulfilment: String, deliveryAddress: Address?, scheduledDate: String) {
        val userId = authRepository.currentUserId
        if (userId == null) {
            _placeOrderResult.value = Result.Error("Please log in again.")
            return
        }
        val items = CartManager.items.value ?: emptyList()
        if (items.isEmpty()) {
            _placeOrderResult.value = Result.Error("Your cart is empty.")
            return
        }
        if (fulfilment == "delivery" && deliveryAddress == null) {
            _placeOrderResult.value = Result.Error("Please choose a delivery address.")
            return
        }
        if (scheduledDate.isBlank()) {
            _placeOrderResult.value = Result.Error("Please choose a date.")
            return
        }

        val order = Order(
            userId = userId,
            items = items,
            fulfilment = fulfilment,
            deliveryAddress = deliveryAddress,
            scheduledDate = scheduledDate,
            totalPrice = CartManager.subtotal() + CartManager.deliveryFee(fulfilment)
        )

        _placeOrderResult.value = Result.Loading
        viewModelScope.launch {
            _placeOrderResult.value = orderRepository.createOrder(order)
        }
    }
}
