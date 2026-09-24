package com.printxpress.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.printxpress.app.data.model.Address
import com.printxpress.app.data.model.SavedDesign
import com.printxpress.app.data.model.User
import com.printxpress.app.util.FirestorePaths
import com.printxpress.app.util.Result
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getUser(uid: String): Result<User> {
        return try {
            val snapshot = firestore.collection(FirestorePaths.USERS).document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
                ?: return Result.Error("Profile not found.")
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not load profile.", e)
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            firestore.collection(FirestorePaths.USERS).document(user.uid)
                .set(user).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not save profile.", e)
        }
    }

    // ---- Addresses: users/{uid}/addresses ----

    suspend fun getAddresses(uid: String): Result<List<Address>> {
        return try {
            val snapshot = firestore.collection(FirestorePaths.USERS).document(uid)
                .collection(FirestorePaths.ADDRESSES).get().await()
            val addresses = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Address::class.java)?.copy(id = doc.id)
            }
            Result.Success(addresses)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not load addresses.", e)
        }
    }

    suspend fun addAddress(uid: String, address: Address): Result<Address> {
        return try {
            val collection = firestore.collection(FirestorePaths.USERS).document(uid)
                .collection(FirestorePaths.ADDRESSES)
            val docRef = collection.document() // generates the ID client-side
            val toSave = address.copy(id = docRef.id)
            docRef.set(toSave).await()
            Result.Success(toSave)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not save address.", e)
        }
    }

    suspend fun deleteAddress(uid: String, addressId: String): Result<Unit> {
        return try {
            firestore.collection(FirestorePaths.USERS).document(uid)
                .collection(FirestorePaths.ADDRESSES).document(addressId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not delete address.", e)
        }
    }

    // ---- Saved designs: users/{uid}/savedDesigns ----

    suspend fun getSavedDesigns(uid: String): Result<List<SavedDesign>> {
        return try {
            val snapshot = firestore.collection(FirestorePaths.USERS).document(uid)
                .collection(FirestorePaths.SAVED_DESIGNS)
                .orderBy("createdAt")
                .get().await()
            val designs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(SavedDesign::class.java)?.copy(id = doc.id)
            }
            Result.Success(designs)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not load saved designs.", e)
        }
    }

    suspend fun deleteSavedDesign(uid: String, designId: String): Result<Unit> {
        return try {
            firestore.collection(FirestorePaths.USERS).document(uid)
                .collection(FirestorePaths.SAVED_DESIGNS).document(designId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not delete design.", e)
        }
    }
}
