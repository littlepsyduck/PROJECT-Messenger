package dev.proptit.kotlinflow.data.mappers

import com.google.firebase.database.DataSnapshot
import dev.proptit.kotlinflow.domain.entities.User
import dev.proptit.kotlinflow.domain.entities.UserStatus
import javax.inject.Inject

class UserMapper @Inject constructor() {
    fun mapFromSnapshot(snapshot: DataSnapshot): User {
        return User(
            id = snapshot.child("id").getValue(String::class.java) ?: "",
            email = snapshot.child("email").getValue(String::class.java) ?: "",
            name = snapshot.child("name").getValue(String::class.java) ?: "",
            status = snapshot.child("status")
                .getValue(String::class.java)
                ?.let { UserStatus.valueOf(it) } 
                ?: UserStatus.OFFLINE,
            fcmToken = snapshot.child("fcmToken").getValue(String::class.java)
        )
    }
} 