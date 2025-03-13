package dev.proptit.kotlinflow.domain.repositories

import dev.proptit.kotlinflow.domain.entities.Chat
import dev.proptit.kotlinflow.domain.entities.Message
import kotlinx.coroutines.flow.Flow

interface IChatRepository {
    suspend fun sendMessage(chatId: String, message: Message): Result<Unit>
    fun getMessages(chatId: String): Flow<List<Message>>
    fun getChats(userId: String): Flow<List<Chat>>
    suspend fun createChat(chat: Chat): Result<String>
    suspend fun updateChat(chat: Chat): Result<Unit>
    suspend fun deleteChat(chatId: String): Result<Unit>
} 