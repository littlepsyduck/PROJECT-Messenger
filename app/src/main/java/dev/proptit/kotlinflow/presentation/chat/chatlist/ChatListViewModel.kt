package dev.proptit.kotlinflow.presentation.chat.chatlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.proptit.kotlinflow.domain.entities.Chat
import dev.proptit.kotlinflow.domain.entities.User
import dev.proptit.kotlinflow.domain.repositories.IAuthRepository
import dev.proptit.kotlinflow.domain.usecases.chat.GetChatsUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getChatsUseCase: GetChatsUseCase,
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _chats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> = _chats

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser()
                .onSuccess { user ->
                    _currentUser.value = user
                    loadChats(user.id)
                }
                .onFailure { error ->
                    _error.value = error.message ?: "Không thể tải thông tin người dùng"
                }
        }
    }

    private fun loadChats(userId: String) {
        viewModelScope.launch {
            getChatsUseCase(userId)
                .catch { error ->
                    _error.value = error.message ?: "Không thể tải danh sách chat"
                }
                .collect { chatList ->
                    _chats.value = chatList
                }
        }
    }
} 