package com.printxpress.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

/**
 * Mirrors a document in the top-level `orders` collection.
 * See Task B, Section B.5 for why orders is top-level rather than a
 * subcollection of users, and why items are embedded rather than
 * a separate collection.
 */
data class Order(
    var id: String = "",
    var userId: String = "",
    var items: List<OrderItem> = emptyList(),
    var status: String = OrderStatus.PROCESSING.name,
    var fulfilment: String = "pickup",
    var deliveryAddress: Address? = null,
    var totalPrice: Double = 0.0,
    var scheduledDate: String = "",
    @ServerTimestamp var createdAt: Timestamp? = null,
    @ServerTimestamp var updatedAt: Timestamp? = null
) {
    /**
     * Recomputes the total from line items plus delivery fee - used by
     * OrderRepository to validate totalPrice before writing, so a client-
     * side bug (or a tampered request) can't persist a mismatched price.
     * The flat delivery fee mirrors CartManager.deliveryFee(); duplicated
     * here (rather than shared) because Order is a plain data model with
     * no dependency on the ui.cart package - see Task F for this noted
     * as a candidate for a shared PricingPolicy object if the app grows.
     */
    @Exclude
    fun calculateTotal(): Double {
        val itemsTotal = items.sumOf { it.lineTotal }
        val deliveryFee = if (fulfilment == "delivery") 250.0 else 0.0
        return itemsTotal + deliveryFee
    }

    /**
     * A customer may only reschedule or cancel before the print shop has
     * started printing - once status is PRINTING or later, the order is
     * locked from the customer's side (see OrderDetailActivity).
     */
    @Exclude
    fun canCancel(): Boolean =
        OrderStatus.fromString(status) == OrderStatus.PROCESSING
}
