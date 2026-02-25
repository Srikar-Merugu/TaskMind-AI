package com.taskmind.app.domain.repository

import com.taskmind.app.domain.model.User

interface AuthRepository {
    fun register(name: String, email: String, password: String, onResult: (Result<User>) -> Unit)
    fun login(email: String, password: String, onResult: (Result<User>) -> Unit)
    fun logout()
    fun getCurrentUser(): User?
}
