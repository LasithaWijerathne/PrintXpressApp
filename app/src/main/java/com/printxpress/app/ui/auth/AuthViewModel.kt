package com.printxpress.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.printxpress.app.data.repository.AuthRepository
import com.printxpress.app.util.Result
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    fun login(emailOrPhone: String, password: String) {
        if (emailOrPhone.isBlank() || password.isBlank()) {
            _loginResult.value = Result.Error("Please enter both email and password.")
            return
        }
        _loginResult.value = Result.Loading
        viewModelScope.launch {
            _loginResult.value = authRepository.login(emailOrPhone, password)
        }
    }

    /**
     * All validation happens here, before any network call, mirroring the
     * server-side-first validation approach used in the GlobeTrek web
     * project's registration route - client validation is for a fast,
     * friendly UI, not the only line of defence (Firestore Security Rules,
     * covered in Task D's repository classes, are the real enforcement).
     */
    fun register(name: String, email: String, phone: String, password: String, confirmPassword: String) {
        val errors = mutableListOf<String>()
        if (name.isBlank()) errors.add("Please enter your name.")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors.add("Please enter a valid email address.")
        }
        if (password.length < 6) errors.add("Password must be at least 6 characters.")
        if (password != confirmPassword) errors.add("Passwords do not match.")

        if (errors.isNotEmpty()) {
            _registerResult.value = Result.Error(errors.first())
            return
        }

        _registerResult.value = Result.Loading
        viewModelScope.launch {
            val result = authRepository.register(name, email, phone, password)
            _registerResult.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message, result.cause)
                is Result.Loading -> Result.Loading
            }
        }
    }
}
