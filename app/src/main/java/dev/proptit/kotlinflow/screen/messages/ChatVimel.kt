package dev.proptit.kotlinflow.screen.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.proptit.kotlinflow.domain.Message
import dev.proptit.kotlinflow.service.MessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatVimel : ViewModel() {
    private val messageService = MessageService()
    private lateinit var currentChatId: String
    private val currentUserId = "user123" // Giả lập userId, có thể thay bằng Firebase Auth hoặc SharedPreferences

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun getCurrentUserId(): String {
        return currentUserId
    }

    fun init(chatId: String) {
        currentChatId = chatId
        viewModelScope.launch {
            messageService.getMessages(chatId)
                .onEach { messages ->
                    _messages.value = messages
                }
                .launchIn(viewModelScope)
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val message = Message(
                id = System.currentTimeMillis().toString(),
                senderId = currentUserId,
                text = text
            )
            messageService.sendMessage(currentChatId, message.toString())
        }
    }
}
