package dev.proptit.kotlinflow.screen.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dev.proptit.kotlinflow.databinding.FragmentChatBinding
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private val binding by lazy {
        FragmentChatBinding.inflate(layoutInflater)
    }
    private val vimel: ChatVimel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatId = requireArguments().getString("chatId")
            ?: throw IllegalArgumentException("chatId is required")
        vimel.init(chatId)

        val currentUserId = vimel.getCurrentUserId()
        val adapter = MessagesAdapter(currentUserId)
        binding.rvMessageList.adapter = adapter

        binding.btnSendMessage.setOnClickListener {
            val text = binding.etMessageInput.text.toString()
            if (text.isNotBlank()) {
                vimel.sendMessage(text)
                binding.etMessageInput.text.clear()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vimel.messages.collect { messages ->
                adapter.submitList(messages)
                if (messages.isNotEmpty()) {
                    binding.rvMessageList.scrollToPosition(messages.size - 1)
                }
            }
        }
    }

}
