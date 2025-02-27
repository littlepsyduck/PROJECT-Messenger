package dev.proptit.kotlinflow.screen.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.proptit.kotlinflow.domain.Chat
import dev.proptit.kotlinflow.service.ChatService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatListVimel: ViewModel() {
    private val chatService = ChatService()
    private val _data = MutableStateFlow<List<Chat>>(emptyList())
    val data = _data.asStateFlow()

    init {
        viewModelScope.launch {
            chatService.getUserChats()
                .onEach {
                    _data.value = it
                    println("🔥 Danh sách chat: $it")

                }
                .launchIn(viewModelScope)
        }
    }
}