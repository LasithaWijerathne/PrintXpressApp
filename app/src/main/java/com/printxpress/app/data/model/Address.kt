package com.printxpress.app.data.model

/**
 * Mirrors a document in the `users/{uid}/addresses` subcollection.
 * Also embedded directly (as a Firestore Map) inside an Order document
 * as `deliveryAddress`, since an order must keep a snapshot of the address
 * used at the time it was placed even if the user edits it afterwards.
 */
data class Address(
    var id: String = "",
    var label: String = "",
    var line1: String = "",
    var city: String = "",
    var isDefault: Boolean = false
)
