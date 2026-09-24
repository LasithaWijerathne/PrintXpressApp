package com.printxpress.app.data.model

/**
 * The four states an order can be in. Stored in Firestore as a plain
 * string (its `name`), not a number, so the raw document is human-readable
 * in the Firebase console. Using an enum in Kotlin (rather than a raw
 * String field) means the compiler catches an invalid status at build
 * time, e.g. a typo, even though Firestore itself does not enforce it.
 */
enum class OrderStatus {
    PROCESSING,
    PRINTING,
    READY_FOR_PICKUP,
    CANCELLED;

    companion object {
        fun fromString(value: String?): OrderStatus =
            entries.firstOrNull { it.name == value } ?: PROCESSING
    }
}
