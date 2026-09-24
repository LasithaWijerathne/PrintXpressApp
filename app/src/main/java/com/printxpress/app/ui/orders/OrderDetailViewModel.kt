package com.printxpress.app.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.printxpress.app.data.model.Order
import com.printxpress.app.data.repository.OrderRepository
import com.printxpress.app.util.Result
import kotlinx.coroutines.launch

class OrderDetailViewModel(
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    private val _order = MutableLiveData<Result<Order>>()
    val order: LiveData<Result<Order>> = _order

    private val _actionResult = MutableLiveData<Result<Unit>>()
    val actionResult: LiveData<Result<Unit>> = _actionResult

    fun loadOrder(orderId: String) {
        _order.value = Result.Loading
        viewModelScope.launch {
            _order.value = orderRepository.getOrder(orderId)
        }
    }

    fun cancelOrder(orderId: String) {
        _actionResult.value = Result.Loading
        viewModelScope.launch {
            val result = orderRepository.cancelOrder(orderId)
            _actionResult.value = result
            if (result is Result.Success) loadOrder(orderId) // refresh to show new status
        }
    }

    fun rescheduleOrder(orderId: String, newDate: String) {
        _actionResult.value = Result.Loading
        viewModelScope.launch {
            val result = orderRepository.rescheduleOrder(orderId, newDate)
            _actionResult.value = result
            if (result is Result.Success) loadOrder(orderId)
        }
    }
}
