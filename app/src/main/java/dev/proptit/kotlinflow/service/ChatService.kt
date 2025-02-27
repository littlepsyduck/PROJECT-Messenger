package dev.proptit.kotlinflow.service

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import dev.proptit.kotlinflow.domain.Chat
import dev.proptit.kotlinflow.domain.Message
import dev.proptit.kotlinflow.dto.ChatDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ChatService {
    private val firebase = Firebase.firestore
    private val auth = Firebase.auth

    // 🆕 Lấy thông tin người dùng từ Firestore
    private suspend fun getUserInfo(userId: String): Pair<String, String> {
        val doc = firebase.collection("users").document(userId).get().await()
        val name = doc.getString("name") ?: "Unknown"
        val avatarUrl = doc.getString("avatarUrl") ?: ""
        return Pair(name, avatarUrl)
    }

    // 🆕 Gửi tin nhắn mới
    suspend fun sendMessage(chatId: String, messageText: String) {
        val currentUserId = auth.currentUser?.uid ?: throw Exception("Not logged in")
        val (senderName, avatarUrl) = getUserInfo(currentUserId)

        val message = Message(
            id = firebase.collection("chats/$chatId/messages").document().id,
            senderId = currentUserId,
            senderName = senderName, // ✅ Lưu tên người gửi
            avatarUrl = avatarUrl,   // ✅ Lưu ảnh đại diện
            text = messageText
        )

        firebase.collection("chats/$chatId/messages").add(message)
    }

    // Lấy danh sách chat của user hiện tại
    fun getUserChats(): Flow<List<Chat>> {
        val currentUserId = auth.currentUser?.uid ?: throw Exception("Not logged in")
        return firebase.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    val messagesQuery = firebase.collection("chats/${doc.id}/messages")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .await()

                    val lastMessageDoc = messagesQuery.documents.firstOrNull()
                    val lastMessageText = lastMessageDoc?.getString("text") ?: ""
                    val lastMessageTime = lastMessageDoc?.getLong("createdAt") ?: 0L

                    doc.toObject(ChatDto::class.java)?.copy(
                        lastMessage = lastMessageText,
                        lastMessageTime = lastMessageTime
                    )?.mapToChat(doc.id)
                }
            }
    }

    suspend fun createChat(participants: List<String>): String {
        val currentUserId = auth.currentUser?.uid ?: throw Exception("Not logged in")

        // Tạo danh sách người tham gia (bao gồm cả user hiện tại)
        val chatParticipants = listOf(currentUserId) + participants

        val chatRef = firebase.collection("chats").document()

        val chatData = mapOf(
            "id" to chatRef.id,
            "participants" to chatParticipants,
            "createdAt" to System.currentTimeMillis()
        )

        chatRef.set(chatData).await()

        return chatRef.id // Trả về ID của cuộc trò chuyện mới
    }

}
