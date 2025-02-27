package dev.proptit.kotlinflow.screen.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dev.proptit.kotlinflow.R
import dev.proptit.kotlinflow.databinding.FragmentChatListBinding
import dev.proptit.kotlinflow.service.ChatService
import kotlinx.coroutines.launch

class ChatListFragment: Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private val vimel by viewModels<ChatListVimel>()
    private val service = ChatService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvChatList.adapter = ChatsAdapter {
            findNavController().navigate(
                R.id.action_chatListFragment_to_chatFragment,
                bundleOf("chatId" to it.id)
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vimel.data.collect { chats ->
                (binding.rvChatList.adapter as ChatsAdapter).submitList(chats)
            }
        }

        binding.btnAddChat.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val chatId = service.createChat(listOf("user2")) // ✅ Gọi `createChat`
                    findNavController().navigate(
                        R.id.action_chatListFragment_to_chatFragment,
                        bundleOf("chatId" to chatId)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Giải phóng binding để tránh memory leak
    }
}
