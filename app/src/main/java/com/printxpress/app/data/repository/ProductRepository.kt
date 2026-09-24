package com.printxpress.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.printxpress.app.data.model.Product
import com.printxpress.app.util.FirestorePaths
import com.printxpress.app.util.Result
import kotlinx.coroutines.tasks.await

class ProductRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val snapshot = firestore.collection(FirestorePaths.PRODUCTS).get().await()
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
            Result.Success(products)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not load products.", e)
        }
    }

    suspend fun getProductsByCategory(category: String): Result<List<Product>> {
        return try {
            val snapshot = firestore.collection(FirestorePaths.PRODUCTS)
                .whereEqualTo("category", category)
                .get().await()
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
            Result.Success(products)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not load this category.", e)
        }
    }

    suspend fun getProduct(productId: String): Result<Product> {
        return try {
            val doc = firestore.collection(FirestorePaths.PRODUCTS).document(productId).get().await()
            val product = doc.toObject(Product::class.java)?.copy(id = doc.id)
                ?: return Result.Error("Product not found.")
            Result.Success(product)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Could not load product.", e)
        }
    }

    /**
     * Client-side substring search over the products already fetched.
     * Firestore has no native full-text search, and this catalogue is
     * small (a handful of product types), so filtering a single already-
     * cached list is simpler and cheaper than a dedicated search service
     * such as Algolia, which would be worth adding only at a much larger
     * catalogue size - noted as a scalability limitation in Task F.
     */
    fun filterByQuery(products: List<Product>, query: String): List<Product> {
        if (query.isBlank()) return products
        val q = query.trim().lowercase()
        return products.filter {
            it.name.lowercase().contains(q) || it.category.lowercase().contains(q)
        }
    }
}
