package com.printxpress.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/**
 * Mirrors a document in the top-level `users` collection.
 * The document ID is always the Firebase Auth UID, so this class does not
 * duplicate a separate id field - see UserRepository for how it's fetched.
 *
 * The no-argument constructor and var properties are required by
 * Firestore's automatic object mapping (toObject<User>()).
 */
data class User(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    /** Latest Firebase Cloud Messaging device token, written by
     *  PrintXpressMessagingService.onNewToken - used server-side by the
     *  Cloud Function to target a push notification at this user's device. */
    var fcmToken: String? = null,
    @ServerTimestamp var createdAt: Timestamp? = null
)
