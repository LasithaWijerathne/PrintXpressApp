package com.printxpress.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.printxpress.app.data.model.AppNotification
import com.printxpress.app.util.FirestorePaths
import com.printxpress.app.util.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /** Live feed of a user's notifications - order updates pushed by the
     *  Cloud Function described in Task B appear here the moment they're
     *  written, without the app needing to poll. */
    fun observeNotifications(userId: String): Flow<Result<List<AppNotification>>> = callbackFlow {
        trySend(Result.Loading)

        val registration = firestore.collection(FirestorePaths.NOTIFICATIONS)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Could not load notifications.", error))
                    return@addSnapshotListener
                }
                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AppNotification::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(Result.Success(notifications))
            }

        awaitClose { registration.remove() }
    }

    suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            firestore.collection(FirestorePaths.NOTIFICATIONS).document(notificationId)
                .update("read", true).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not update notification.", e)
        }
    }
}
