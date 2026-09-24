package com.printxpress.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/**
 * Mirrors a document in the top-level `notifications` collection.
 * Named AppNotification (not Notification) to avoid clashing with
 * android.app.Notification, which is used elsewhere in this codebase
 * (PrintXpressMessagingService) to build the actual system notification.
 */
data class AppNotification(
    var id: String = "",
    var userId: String = "",
    var title: String = "",
    var message: String = "",
    var type: String = "order_update", // "order_update" | "promo"
    var read: Boolean = false,
    @ServerTimestamp var createdAt: Timestamp? = null
)
