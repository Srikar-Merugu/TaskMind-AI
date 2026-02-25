package com.taskmind.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.taskmind.app.domain.model.User
import com.taskmind.app.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun register(
        name: String,
        email: String,
        password: String,
        onResult: (Result<User>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val user = User(uid = uid, name = name, email = email)
                
                // Save name to Realtime Database
                database.child("users").child(uid).child("name").setValue(name)
                    .addOnSuccessListener {
                        onResult(Result.success(user))
                    }
                    .addOnFailureListener {
                        onResult(Result.failure(it))
                    }
            }
            .addOnFailureListener {
                onResult(Result.failure(it))
            }
    }

    override fun login(email: String, password: String, onResult: (Result<User>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val email = result.user?.email ?: ""
                // We'll fetch the name separately in the ViewModel or MainActivity
                onResult(Result.success(User(uid = uid, email = email)))
            }
            .addOnFailureListener {
                onResult(Result.failure(it))
            }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return firebaseUser?.let {
            User(uid = it.uid, email = it.email ?: "")
        }
    }
}
