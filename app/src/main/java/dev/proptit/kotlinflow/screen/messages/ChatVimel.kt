package dev.proptit.kotlinflow.screen.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.proptit.kotlinflow.domain.Message
import dev.proptit.kotlinflow.service.MessageService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatVimel: ViewModel() {
    private val messageService = MessageService()
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _newMessage = MutableSharedFlow<Message>()
    val newMessage = _newMessage.asSharedFlow()

    fun init(chatId: String) {
        viewModelScope.launch {
            messageService.collectMessageUpdate(chatId) { change, message ->
                _newMessage.emit(message)
            }
        }
        viewModelScope.launch {
            fetchMessages(chatId)
        }
    }

    private suspend fun fetchMessages(chatId: String) {
        _messages.value = messageService.getMessages(chatId = chatId)
    }
}