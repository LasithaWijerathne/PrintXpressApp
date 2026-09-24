package com.printxpress.app.util

/**
 * A simple wrapper every repository function returns instead of throwing
 * exceptions across layers. UI code always has exactly three cases to
 * handle - Loading, Success, Error - which keeps every ViewModel's
 * network/database handling consistent throughout the app.
 */
sealed class Result<out T> {
    data object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>()
}
