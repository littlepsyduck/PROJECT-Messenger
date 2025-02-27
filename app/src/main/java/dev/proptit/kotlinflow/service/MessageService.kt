package dev.proptit.kotlinflow.service

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import dev.proptit.kotlinflow.domain.Message
import dev.proptit.kotlinflow.dto.MessageDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class MessageService {
    private val firebase = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun sendMessage(chatId: String, text: String) {
        val currentUserId = auth.currentUser?.uid ?: throw Exception("Not logged in")
        val message = MessageDto(
            senderId = currentUserId,
            text = text
        )

        firebase.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)
            .await()
    }

    fun getMessages(chatId: String): Flow<List<Message>> {
        return firebase.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.toObject(MessageDto::class.java)?.mapToMessage(doc.id)
                        ?: throw Exception("Invalid message data")
                }
            }
    }
}