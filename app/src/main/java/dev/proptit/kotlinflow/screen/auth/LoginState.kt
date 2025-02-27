package dev.proptit.kotlinflow.screen.auth

sealed class LoginState {
    data class Success(val user: Any) : LoginState()
    data class Error(val message: String) : LoginState()
    data class Loading(val isLoading: Boolean) : LoginState()
}