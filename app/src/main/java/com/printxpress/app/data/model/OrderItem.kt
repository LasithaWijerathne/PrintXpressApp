package com.printxpress.app.data.model

/**
 * A single line item within an Order. Stored as a nested Map inside the
 * parent order's `items` array field (see Task B, Firestore data model) -
 * never as its own Firestore document, since it has no meaning outside
 * the order it belongs to and is always read/written together with it.
 */
data class OrderItem(
    var productId: String = "",
    var productName: String = "",
    var quantity: Int = 1,
    var paperType: String = "",
    var size: String = "",
    var unitPrice: Double = 0.0,
    var artworkUrl: String? = null
) {
    val lineTotal: Double get() = unitPrice * quantity
}
