package dev.proptit.kotlinflow.domain.chat

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.proptit.kotlinflow.domain.model.Chat
import dev.proptit.kotlinflow.domain.model.Message
import dev.proptit.kotlinflow.domain.model.MessageStatus
import dev.proptit.kotlinflow.domain.model.User
import dev.proptit.kotlinflow.notification.NotificationHelper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatManager {
    private val database = FirebaseDatabase.getInstance().reference
    private val TAG = "ChatManager"

    suspend fun createChat(participants: List<String>): String {
        return try {
            // Kiểm tra xem chat đã tồn tại chưa
            val existingChatId = findExistingChat(participants)
            if (existingChatId != null) {
                Log.d(TAG, "Found existing chat: $existingChatId")
                return existingChatId
            }

            // Tạo chat mới nếu chưa tồn tại
            val chatId = UUID.randomUUID().toString()
            val chat = Chat(
                id = chatId,
                participants = participants,
                timestamp = System.currentTimeMillis()
            )
            database.child("chats").child(chatId).setValue(chat).await()
            Log.d(TAG, "Created new chat: $chatId")
            chatId
        } catch (e: Exception) {
            Log.e(TAG, "Error creating chat", e)
            throw e
        }
    }

    private suspend fun findExistingChat(participants: List<String>): String? {
        return try {
            val snapshot = database.child("chats").get().await()
            snapshot.children.find { chatSnapshot ->
                val chat = chatSnapshot.getValue(Chat::class.java)
                chat?.participants?.containsAll(participants) == true && 
                chat.participants.size == participants.size
            }?.key
        } catch (e: Exception) {
            Log.e(TAG, "Error finding existing chat", e)
            null
        }
    }

    suspend fun sendMessage(chatId: String, senderId: String, receiverId: String, content: String) {
        try {
            Log.d(TAG, "Sending message in chat: $chatId")
            val messageId = UUID.randomUUID().toString()
            val message = Message(
                id = messageId,
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.SENT
            )
            
            // Lưu tin nhắn vào node messages của chat
            database.child("chats").child(chatId).child("messages").child(messageId)
                .setValue(message).await()
            
            // Cập nhật last message và timestamp của chat
            database.child("chats").child(chatId).child("lastMessage").setValue(message).await()
            database.child("chats").child(chatId).child("timestamp")
                .setValue(message.timestamp).await()
            
            // Gửi thông báo
            sendNotificationToReceiver(chatId, messageId, senderId, receiverId, content)
            
            Log.d(TAG, "Message sent successfully: $messageId")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            throw e
        }
    }

    private suspend fun sendNotificationToReceiver(
        chatId: String,
        messageId: String,
        senderId: String,
        receiverId: String,
        content: String
    ) {
        try {
            // Lấy thông tin người gửi và người nhận
            val senderSnapshot = database.child("users").child(senderId).get().await()
            val receiverSnapshot = database.child("users").child(receiverId).get().await()
            
            val sender = senderSnapshot.getValue(User::class.java)
            val receiver = receiverSnapshot.getValue(User::class.java)
            
            if (receiver?.fcmToken != null) {
                val notificationData = mapOf(
                    "chatId" to chatId,
                    "messageId" to messageId,
                    "senderId" to senderId,
                    "receiverId" to receiverId,
                    "title" to "${sender?.name ?: "Người dùng"}",
                    "message" to content
                )
                
                val success = NotificationHelper.sendNotification(
                    token = receiver.fcmToken,
                    title = "${sender?.name ?: "Người dùng"}",
                    message = content,
                    data = notificationData
                )
                
                if (success) {
                    Log.d(TAG, "Notification sent to user: $receiverId")
                } else {
                    Log.e(TAG, "Failed to send notification to user: $receiverId")
                }
            } else {
                Log.d(TAG, "Receiver FCM token not found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending notification", e)
        }
    }

    fun getMessagesFlow(chatId: String): Flow<List<Message>> = callbackFlow {
        Log.d(TAG, "Starting to observe messages for chat: $chatId")
        val messagesRef = database.child("chats").child(chatId).child("messages")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val messages = snapshot.children.mapNotNull { 
                        it.getValue(Message::class.java) 
                    }.sortedBy { it.timestamp }
                    Log.d(TAG, "Received ${messages.size} messages")
                    trySend(messages)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing messages", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Messages listener cancelled", error.toException())
            }
        }

        messagesRef.addValueEventListener(listener)
        awaitClose { 
            Log.d(TAG, "Removing messages listener")
            messagesRef.removeEventListener(listener) 
        }
    }

    fun getChatListFlow(userId: String): Flow<List<Chat>> = callbackFlow {
        Log.d(TAG, "Starting to observe chats for user: $userId")
        val chatsRef = database.child("chats")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val chats = snapshot.children.mapNotNull { 
                        it.getValue(Chat::class.java) 
                    }.filter { 
                        it.participants.contains(userId) 
                    }.sortedByDescending { it.timestamp }
                    Log.d(TAG, "Received ${chats.size} chats")
                    trySend(chats)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing chats", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Chats listener cancelled", error.toException())
            }
        }

        chatsRef.addValueEventListener(listener)
        awaitClose { 
            Log.d(TAG, "Removing chats listener")
            chatsRef.removeEventListener(listener) 
        }
    }

    suspend fun updateMessageStatus(chatId: String, messageId: String, status: MessageStatus) {
        try {
            database.child("chats").child(chatId).child("messages").child(messageId)
                .child("status").setValue(status).await()
            Log.d(TAG, "Message status updated: $messageId -> $status")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating message status", e)
            throw e
        }
    }
} 