package dev.proptit.kotlinflow.presentation.chat.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.proptit.kotlinflow.domain.entities.Message
import dev.proptit.kotlinflow.domain.entities.User
import dev.proptit.kotlinflow.domain.repositories.IAuthRepository
import dev.proptit.kotlinflow.domain.repositories.IChatRepository
import dev.proptit.kotlinflow.domain.usecases.chat.GetMessagesUseCase
import dev.proptit.kotlinflow.domain.usecases.chat.SendMessageUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var chatId: String? = null

    init {
        loadCurrentUser()
    }

    fun setChatId(id: String) {
        chatId = id
        loadMessages()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser()
                .onSuccess { user ->
                    _currentUser.value = user
                }
                .onFailure { error ->
                    _error.value = error.message ?: "Không thể tải thông tin người dùng"
                }
        }
    }

    private fun loadMessages() {
        chatId?.let { id ->
            viewModelScope.launch {
                getMessagesUseCase(id)
                    .catch { error ->
                        _error.value = error.message ?: "Không thể tải tin nhắn"
                    }
                    .collect { messageList ->
                        _messages.value = messageList
                    }
            }
        }
    }

    fun sendMessage(content: String) {
        val currentUser = _currentUser.value ?: return
        val chatId = chatId ?: return

        val message = Message(
            id = System.currentTimeMillis().toString(),
            chatId = chatId,
            senderId = currentUser.id,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            sendMessageUseCase(chatId, message)
                .onSuccess {
                    // Tin nhắn đã được gửi thành công
                }
                .onFailure { error ->
                    _error.value = error.message ?: "Không thể gửi tin nhắn"
                }
        }
    }
} 