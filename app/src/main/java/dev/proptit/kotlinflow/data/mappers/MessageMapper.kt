package dev.proptit.kotlinflow.data.mappers

import com.google.firebase.database.DataSnapshot
import dev.proptit.kotlinflow.domain.entities.Message
import dev.proptit.kotlinflow.domain.entities.MessageStatus
import javax.inject.Inject

class MessageMapper @Inject constructor() {
    fun mapFromSnapshot(snapshot: DataSnapshot): Message {
        return Message(
            id = snapshot.child("id").getValue(String::class.java) ?: "",
            senderId = snapshot.child("senderId").getValue(String::class.java) ?: "",
            receiverId = snapshot.child("receiverId").getValue(String::class.java) ?: "",
            content = snapshot.child("content").getValue(String::class.java) ?: "",
            timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L,
            status = snapshot.child("status")
                .getValue(String::class.java)
                ?.let { MessageStatus.valueOf(it) }
                ?: MessageStatus.SENT
        )
    }
} 