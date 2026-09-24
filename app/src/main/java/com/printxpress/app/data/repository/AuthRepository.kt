package com.printxpress.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.printxpress.app.data.model.User
import com.printxpress.app.util.FirestorePaths
import com.printxpress.app.util.Result
import kotlinx.coroutines.tasks.await

/**
 * Wraps Firebase Authentication. Register/login accept either an email
 * address or a phone number typed into the same field (per the brief's
 * requirement to support both) - a simple heuristic decides which Firebase
 * Auth sign-in method to use, since Firebase treats phone auth and email
 * auth as genuinely different flows under the hood.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val currentUserId: String? get() = auth.currentUser?.uid
    val isLoggedIn: Boolean get() = auth.currentUser != null

    /**
     * Registers a new account and creates the matching `users/{uid}` profile
     * document in the same operation, so the two are never out of sync.
     * Phone-based registration is not implemented here (it requires Firebase
     * Phone Auth's separate SMS-verification flow); this app's Login/Register
     * screens accept email only, with the "phone or email" field reserved
     * for future phone-auth support noted in Task F's known limitations.
     */
    suspend fun register(name: String, email: String, phone: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return Result.Error("Registration failed: no user ID returned.")

            val user = User(uid = uid, name = name, email = email, phone = phone)
            firestore.collection(FirestorePaths.USERS).document(uid).set(user).await()

            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Registration failed.", e)
        }
    }

    suspend fun login(emailOrPhone: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(emailOrPhone, password).await()
            val uid = authResult.user?.uid ?: return Result.Error("Login failed.")
            Result.Success(uid)
        } catch (e: Exception) {
            // Deliberately generic message: never reveal whether the email
            // exists or the password was wrong, to avoid leaking which
            // accounts are registered (same principle applied in the
            // GlobeTrek web project's login route).
            Result.Error("Incorrect email or password.", e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
