package dev.proptit.kotlinflow.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.proptit.kotlinflow.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val authService = AuthService()
    private val _state = MutableStateFlow<LoginState>(LoginState.Loading(false))
    val state = _state.asStateFlow()

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _state.value = LoginState.Loading(true)
                val user = authService.signUp(email, password, name)
                _state.value = LoginState.Success(user)
            } catch (e: Exception) {
                _state.value = LoginState.Error(e.message ?: "Unknown error")
            } finally {
                _state.value = LoginState.Loading(false)
            }
        }
    }
}