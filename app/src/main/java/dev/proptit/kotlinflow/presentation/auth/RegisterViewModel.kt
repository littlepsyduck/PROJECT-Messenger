package dev.proptit.kotlinflow.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.proptit.kotlinflow.domain.entities.User
import dev.proptit.kotlinflow.domain.usecases.auth.RegisterUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            
            registerUseCase(email, password, name)
                .onSuccess { user ->
                    _registerState.value = RegisterState.Success(user)
                }
                .onFailure { error ->
                    _registerState.value = RegisterState.Error(error.message ?: "Đăng ký thất bại")
                }
        }
    }
}

sealed class RegisterState {
    object Loading : RegisterState()
    data class Success(val user: User) : RegisterState()
    data class Error(val message: String) : RegisterState()
} 