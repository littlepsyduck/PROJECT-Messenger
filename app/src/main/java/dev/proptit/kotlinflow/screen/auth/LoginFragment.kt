package dev.proptit.kotlinflow.screen.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dev.proptit.kotlinflow.MainActivity
import dev.proptit.kotlinflow.R
import dev.proptit.kotlinflow.domain.auth.AuthManager
import dev.proptit.kotlinflow.domain.model.UserStatus
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireActivity())

        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        tvRegister = view.findViewById(R.id.tvRegister)

        // Check if user is already logged in
        if (authManager.getCurrentUser() != null) {
            (activity as? MainActivity)?.showChatList()
            return
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    btnLogin.isEnabled = false
                    val result = authManager.login(email, password)
                    result.onSuccess {
                        authManager.updateUserStatus(UserStatus.ONLINE)
                        (activity as? MainActivity)?.showChatList()
                    }.onFailure {
                        Toast.makeText(context, "Đăng nhập thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    btnLogin.isEnabled = true
                }
            }
        }

        tvRegister.setOnClickListener {
            (activity as? MainActivity)?.showFragment(RegisterFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            authManager.updateUserStatus(UserStatus.OFFLINE)
        }
    }
}