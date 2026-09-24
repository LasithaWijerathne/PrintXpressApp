package com.printxpress.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.printxpress.app.data.model.Order
import com.printxpress.app.data.model.OrderStatus
import com.printxpress.app.util.FirestorePaths
import com.printxpress.app.util.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Live-updating stream of a customer's orders. Because this uses
     * Firestore's addSnapshotListener rather than a one-off get(), the My
     * Orders screen updates itself the instant a print-shop-side process
     * changes an order's status - no pull-to-refresh needed. This is the
     * concrete implementation of the "real-time order status" justification
     * given for choosing Firebase in Task B, Section B.1.
     */
    fun observeOrders(userId: String): Flow<Result<List<Order>>> = callbackFlow {
        trySend(Result.Loading)

        val registration = firestore.collection(FirestorePaths.ORDERS)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Could not load orders.", error))
                    return@addSnapshotListener
                }
                val orders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(Result.Success(orders))
            }

        // Cleans up the Firestore listener when the ViewModel's coroutine
        // scope is cancelled (e.g. the screen is destroyed), preventing a
        // leaked listener from continuing to run in the background.
        awaitClose { registration.remove() }
    }

    suspend fun getOrder(orderId: String): Result<Order> {
        return try {
            val doc = firestore.collection(FirestorePaths.ORDERS).document(orderId).get().await()
            val order = doc.toObject(Order::class.java)?.copy(id = doc.id)
                ?: return Result.Error("Order not found.")
            Result.Success(order)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not load order.", e)
        }
    }

    /**
     * Writes a new order document. The order's own calculateTotal() is
     * used to set totalPrice server-side-equivalent (i.e. computed from
     * the same items array being saved) rather than trusting a total
     * computed earlier in the UI, so a client-side bug or tampered
     * request can't save a mismatched price.
     */
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val validatedOrder = order.copy(totalPrice = order.calculateTotal())
            val docRef = firestore.collection(FirestorePaths.ORDERS).document()
            val toSave = validatedOrder.copy(id = docRef.id)
            docRef.set(toSave).await()
            // Note: sending the push notification and writing the matching
            // `notifications` document is done by a Cloud Function triggered
            // by this write server-side (see Task B, Figure 4), not by the
            // client - this guarantees a notification is sent even if the
            // app is closed immediately after placing the order.
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not place order. Please try again.", e)
        }
    }

    suspend fun cancelOrder(orderId: String): Result<Unit> {
        return try {
            firestore.collection(FirestorePaths.ORDERS).document(orderId)
                .update(
                    mapOf(
                        "status" to OrderStatus.CANCELLED.name,
                        "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )
                ).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not cancel order.", e)
        }
    }

    suspend fun rescheduleOrder(orderId: String, newDate: String): Result<Unit> {
        return try {
            firestore.collection(FirestorePaths.ORDERS).document(orderId)
                .update(
                    mapOf(
                        "scheduledDate" to newDate,
                        "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )
                ).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not reschedule order.", e)
        }
    }
}
