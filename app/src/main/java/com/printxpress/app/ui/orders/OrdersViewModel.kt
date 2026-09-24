package com.printxpress.app.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.printxpress.app.data.model.Order
import com.printxpress.app.data.repository.AuthRepository
import com.printxpress.app.data.repository.OrderRepository
import com.printxpress.app.util.Result
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _orders = MutableLiveData<Result<List<Order>>>()
    val orders: LiveData<Result<List<Order>>> = _orders

    /**
     * Starts a live Firestore listener (see OrderRepository.observeOrders).
     * Every emission - not just the first - updates _orders, which is what
     * makes the list refresh itself automatically when an order's status
     * changes, with no manual refresh action anywhere in this screen.
     */
    fun startObservingOrders() {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            orderRepository.observeOrders(userId).collect { result ->
                _orders.value = result
            }
        }
    }
}
