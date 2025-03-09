package dev.proptit.kotlinflow.domain.chat

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.proptit.kotlinflow.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserManager {
    private val database = FirebaseDatabase.getInstance().reference

    fun getUsersFlow(): Flow<List<User>> = callbackFlow {
        val usersRef = database.child("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { 
                    it.getValue(User::class.java) 
                }.sortedBy { it.name }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }
} 