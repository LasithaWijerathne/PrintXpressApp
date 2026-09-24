package com.printxpress.app.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.printxpress.app.util.ALLOWED_ARTWORK_MIME_TYPES
import com.printxpress.app.util.MAX_ARTWORK_FILE_SIZE_BYTES
import com.printxpress.app.util.Result
import kotlinx.coroutines.tasks.await

/**
 * Handles uploading customer artwork files to Firebase Storage.
 * Firestore order/design documents only ever store the resulting download
 * URL (a String) - the binary file itself always lives here, never in a
 * Firestore document, per the size-limit reasoning in Task B, Section B.5.
 */
class StorageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    /**
     * Validates the file's type and size using the ContentResolver *before*
     * starting any network upload, so an obviously invalid file is rejected
     * immediately with a clear message rather than failing midway through
     * a slow upload (mirrors the file-type/size validation approach used
     * for photo uploads in the GlobeTrek web project's Task 2).
     */
    fun validateFile(resolver: ContentResolver, uri: Uri): Result<Unit> {
        val mimeType = resolver.getType(uri)
        if (mimeType !in ALLOWED_ARTWORK_MIME_TYPES) {
            return Result.Error("Please choose a JPG, PNG, or PDF file.")
        }
        val size = resolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: -1L
        if (size in 0..MAX_ARTWORK_FILE_SIZE_BYTES) {
            return Result.Success(Unit)
        }
        return if (size < 0) {
            Result.Success(Unit) // size unknown (e.g. some content providers); allow, Storage still enforces app-level limits
        } else {
            Result.Error("That file is too large. Please use a file under 10MB.")
        }
    }

    suspend fun uploadArtwork(storagePath: String, fileUri: Uri): Result<String> {
        return try {
            val ref = storage.reference.child(storagePath)
            ref.putFile(fileUri).await()
            val downloadUrl = ref.downloadUrl.await()
            Result.Success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.Error("Upload failed. Check the file and try again.", e)
        }
    }
}
