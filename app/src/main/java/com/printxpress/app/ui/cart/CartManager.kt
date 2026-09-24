package com.printxpress.app.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.printxpress.app.data.model.OrderItem

/**
 * Holds the cart in memory only, for the current app session - it is
 * intentionally not persisted to Firestore until checkout succeeds, so a
 * customer who is still deciding what to order never creates a partial
 * order document. This is a plain singleton object rather than a Room/
 * SharedPreferences-backed store: the cart is short-lived, per-session
 * state, not data that needs to survive an app close (unlike orders,
 * addresses and saved designs, which are persisted to Firestore).
 */
object CartManager {

    private val _items = MutableLiveData<List<OrderItem>>(emptyList())
    val items: LiveData<List<OrderItem>> = _items

    fun addItem(item: OrderItem) {
        _items.value = (_items.value ?: emptyList()) + item
    }

    fun removeItem(index: Int) {
        val current = _items.value ?: return
        if (index !in current.indices) return
        _items.value = current.toMutableList().also { it.removeAt(index) }
    }

    fun clear() {
        _items.value = emptyList()
    }

    fun subtotal(): Double = (_items.value ?: emptyList()).sumOf { it.lineTotal }

    /** Flat delivery fee; kept simple deliberately - a real deployment
     *  would likely price this by weight/distance, out of scope here. */
    fun deliveryFee(fulfilment: String): Double = if (fulfilment == "delivery") 250.0 else 0.0
}
