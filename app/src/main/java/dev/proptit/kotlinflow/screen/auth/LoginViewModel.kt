package dev.proptit.kotlinflow.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dev.proptit.kotlinflow.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authService = AuthService()
    private val _state = MutableStateFlow<LoginState>(LoginState.Loading(false))
    val state = _state.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = LoginState.Loading(true)
                val user = authService.signIn(email, password)
                Log.d("AUTH", "Đăng nhập thành công: ${user.id}")
                _state.value = LoginState.Success(user)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _state.value = LoginState.Error("Email hoặc mật khẩu không đúng")
            } catch (e: FirebaseAuthInvalidUserException) {
                _state.value = LoginState.Error("Tài khoản không tồn tại hoặc bị vô hiệu hóa")
            } catch (e: Exception) {
                Log.e("AUTH", "Lỗi đăng nhập: ${e.message}", e)
                _state.value = LoginState.Error("Lỗi không xác định: ${e.message}")
            } finally {
                _state.value = LoginState.Loading(false)
            }
        }
    }

}