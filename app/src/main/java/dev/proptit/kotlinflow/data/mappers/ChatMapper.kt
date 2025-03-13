package dev.proptit.kotlinflow.data.mappers

import com.google.firebase.database.DataSnapshot
import dev.proptit.kotlinflow.domain.entities.Chat
import dev.proptit.kotlinflow.domain.entities.Message
import javax.inject.Inject

class ChatMapper @Inject constructor(
    private val messageMapper: MessageMapper
) {
    fun mapFromSnapshot(snapshot: DataSnapshot): Chat {
        val participants = snapshot.child("participants")
            .children
            .mapNotNull { it.key }
        
        val lastMessageSnapshot = snapshot.child("lastMessage")
        val lastMessage = if (lastMessageSnapshot.exists()) {
            messageMapper.mapFromSnapshot(lastMessageSnapshot)
        } else null
        
        return Chat(
            id = snapshot.key ?: "",
            participants = participants,
            lastMessage = lastMessage,
            unreadCount = snapshot.child("unreadCount").getValue(Int::class.java) ?: 0,
            timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L
        )
    }
} 