package dev.proptit.kotlinflow.screen.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.proptit.kotlinflow.R
import dev.proptit.kotlinflow.domain.auth.AuthManager
import dev.proptit.kotlinflow.domain.chat.ChatManager
import dev.proptit.kotlinflow.screen.chat.adapter.MessageAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private lateinit var toolbar: Toolbar
    private lateinit var tvTitle: TextView
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var authManager: AuthManager
    private val chatManager = ChatManager()
    private var chatId: String? = null
    private var receiverId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireActivity())

        toolbar = view.findViewById(R.id.toolbar)
        tvTitle = view.findViewById(R.id.tvTitle)
        rvMessages = view.findViewById(R.id.rvMessages)
        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)

        // Get chat info from arguments
        chatId = arguments?.getString("chatId")
        receiverId = arguments?.getString("receiverId")
        val receiverName = arguments?.getString("receiverName")

        if (chatId == null || receiverId == null) {
            activity?.onBackPressed()
            return
        }

        tvTitle.text = receiverName ?: "Chat"
        setupToolbar()
        setupRecyclerView()
        setupMessageSending()
        observeMessages()
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(authManager.getCurrentUser()?.uid ?: "")
        rvMessages.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun setupMessageSending() {
        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                etMessage.text.clear()
            }
        }
    }

    private fun sendMessage(content: String) {
        val currentUserId = authManager.getCurrentUser()?.uid ?: return
        chatId?.let { chatId ->
            receiverId?.let { receiverId ->
                lifecycleScope.launch {
                    chatManager.sendMessage(chatId, currentUserId, receiverId, content)
                }
            }
        }
    }

    private fun observeMessages() {
        chatId?.let { chatId ->
            chatManager.getMessagesFlow(chatId)
                .onEach { messages ->
                    messageAdapter.updateMessages(messages)
                    rvMessages.scrollToPosition(messages.size - 1)
                }
                .launchIn(lifecycleScope)
        }
    }
} 