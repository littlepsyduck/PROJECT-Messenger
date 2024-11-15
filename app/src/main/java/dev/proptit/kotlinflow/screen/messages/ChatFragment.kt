package dev.proptit.kotlinflow.screen.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dev.proptit.kotlinflow.databinding.FragmentChatBinding
import kotlinx.coroutines.launch

class ChatFragment: Fragment() {
    private val vimel by viewModels<ChatVimel>()
    private val binding by lazy {
        FragmentChatBinding.inflate(LayoutInflater.from(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvChatList.adapter = MessagesAdapter()

        val chatId = requireArguments().getString("chatId") ?: throw IllegalArgumentException("chatId is required")
        vimel.init(chatId)

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                vimel.newMessage.collect {
                    Toast.makeText(context, "New message: $it", Toast.LENGTH_SHORT).show()
                }
            }
            launch {
                vimel.messages.collect {
                    (binding.rvChatList.adapter as MessagesAdapter).submitList(it)
                }
            }
        }
    }
}