package com.printxpress.app.data.model

/**
 * Mirrors a document in the top-level `products` collection.
 * Products are seeded/managed outside the customer app (e.g. via the
 * Firebase console or an admin tool) - this app only ever reads them.
 */
data class Product(
    var id: String = "",
    var name: String = "",
    var category: String = "",
    var description: String = "",
    var basePrice: Double = 0.0,
    var paperTypes: List<String> = emptyList(),
    var sizes: List<String> = emptyList(),
    var sampleImageUrl: String = ""
)
