package com.taskmind.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taskmind.app.data.repository.AuthRepositoryImpl
import com.taskmind.app.domain.model.User
import com.taskmind.app.domain.repository.AuthRepository

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class RegisterSuccess(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val repository: AuthRepository = AuthRepositoryImpl()

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    // Add session management
    private val _currentUser = MutableLiveData<User?>(repository.getCurrentUser())
    val currentUser: LiveData<User?> = _currentUser

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        repository.login(email, password) { result ->
            result.onSuccess {
                _authState.value = AuthState.Success(it)
                _currentUser.postValue(it)
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPass: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }
        if (password != confirmPass) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        _authState.value = AuthState.Loading
        repository.register(name, email, password) { result ->
            result.onSuccess {
                _authState.value = AuthState.RegisterSuccess("Registration Successful")
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
