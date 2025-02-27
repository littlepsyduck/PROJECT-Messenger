package dev.proptit.kotlinflow.service

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dev.proptit.kotlinflow.domain.User
import dev.proptit.kotlinflow.dto.UserDto
import kotlinx.coroutines.tasks.await

class AuthService {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    suspend fun signIn(email: String, password: String): User {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Authentication failed")
            return getUserData(uid)
        } catch (e: Exception) {
            throw Exception("Sign in failed: ${e.message}")
        }
    }

    suspend fun signUp(email: String, password: String, name: String): User {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Registration failed")

            val userDto = UserDto(email, name)
            firestore.collection("users").document(uid).set(userDto).await()

            return userDto.toUser(uid)
        } catch (e: Exception) {
            throw Exception("Sign up failed: ${e.message}")
        }
    }

    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return getUserData(firebaseUser.uid)
    }

    private suspend fun getUserData(uid: String): User {
        val snapshot = firestore.collection("users").document(uid).get().await()
        val userDto = snapshot.toObject(UserDto::class.java)
            ?: throw Exception("User data not found")
        return userDto.toUser(uid)
    }

    fun signOut() {
        auth.signOut()
    }
}