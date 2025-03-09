package dev.proptit.kotlinflow.screen.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dev.proptit.kotlinflow.MainActivity
import dev.proptit.kotlinflow.R
import dev.proptit.kotlinflow.domain.auth.AuthManager
import dev.proptit.kotlinflow.domain.model.UserStatus
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireActivity())

        etName = view.findViewById(R.id.etName)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnRegister = view.findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    btnRegister.isEnabled = false
                    val result = authManager.register(email, password, name)
                    result.onSuccess {
                        Toast.makeText(context, "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.", Toast.LENGTH_LONG).show()
                        authManager.updateUserStatus(UserStatus.ONLINE)
                        (activity as? MainActivity)?.showChatList()
                    }.onFailure {
                        Toast.makeText(context, "Đăng ký thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    btnRegister.isEnabled = true
                }
            }
        }
    }
}
