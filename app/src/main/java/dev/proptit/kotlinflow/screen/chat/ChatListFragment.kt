package dev.proptit.kotlinflow.screen.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.proptit.kotlinflow.MainActivity
import dev.proptit.kotlinflow.R
import dev.proptit.kotlinflow.domain.auth.AuthManager
import dev.proptit.kotlinflow.domain.chat.ChatManager
import dev.proptit.kotlinflow.domain.chat.UserManager
import dev.proptit.kotlinflow.domain.model.User
import dev.proptit.kotlinflow.screen.chat.adapter.UserAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatListFragment : Fragment() {
    private lateinit var toolbar: Toolbar
    private lateinit var rvChatList: RecyclerView
    private lateinit var userAdapter: UserAdapter

    private lateinit var authManager: AuthManager
    private val chatManager = ChatManager()
    private val userManager = UserManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireActivity())

        toolbar = view.findViewById(R.id.toolbar)
        rvChatList = view.findViewById(R.id.rvChatList)

        setupRecyclerView()
        observeUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter { user ->
            startChatWithUser(user)
        }

        rvChatList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }
    }

    private fun observeUsers() {
        val currentUserId = authManager.getCurrentUser()?.uid ?: return
        userManager.getUsersFlow()
            .onEach { users ->
                // Lọc bỏ người dùng hiện tại khỏi danh sách
                val filteredUsers = users.filter { it.id != currentUserId }
                userAdapter.updateUsers(filteredUsers)
            }
            .launchIn(lifecycleScope)
    }

    private fun startChatWithUser(user: User) {
        val currentUserId = authManager.getCurrentUser()?.uid ?: return
        
        lifecycleScope.launch {
            // Tạo hoặc lấy ID chat hiện có
            val chatId = chatManager.createChat(listOf(currentUserId, user.id))
            
            // Chuyển sang màn hình chat
            val chatFragment = ChatFragment().apply {
                arguments = Bundle().apply {
                    putString("chatId", chatId)
                    putString("receiverId", user.id)
                    putString("receiverName", user.name)
                }
            }
            (activity as? MainActivity)?.showFragment(chatFragment)
        }
    }
} 