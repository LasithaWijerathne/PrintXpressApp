package com.printxpress.app.util

/** Firestore collection names and Storage path prefixes, in one place so
 *  a rename never requires hunting through every repository file. */
object FirestorePaths {
    const val USERS = "users"
    const val ADDRESSES = "addresses"        // subcollection under users/{uid}
    const val SAVED_DESIGNS = "savedDesigns" // subcollection under users/{uid}
    const val PRODUCTS = "products"
    const val ORDERS = "orders"
    const val NOTIFICATIONS = "notifications"
}

object StoragePaths {
    fun artworkPath(orderId: String, fileName: String) = "artwork/$orderId/$fileName"
    fun designPath(uid: String, fileName: String) = "designs/$uid/$fileName"
}

object IntentKeys {
    const val PRODUCT_ID = "extra_product_id"
    const val ORDER_ID = "extra_order_id"
}

/** File types accepted for artwork uploads, enforced client-side before
 *  the file ever reaches Firebase Storage (see ProductDetailActivity). */
val ALLOWED_ARTWORK_MIME_TYPES = setOf("image/jpeg", "image/png", "application/pdf")
const val MAX_ARTWORK_FILE_SIZE_BYTES = 10L * 1024 * 1024 // 10MB
