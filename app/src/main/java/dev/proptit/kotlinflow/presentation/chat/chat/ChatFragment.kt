package dev.proptit.kotlinflow.presentation.chat.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.proptit.kotlinflow.databinding.FragmentChatBinding
import dev.proptit.kotlinflow.presentation.chat.chat.adapter.MessageAdapter

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeState()

        // Lấy chatId từ arguments
        arguments?.getString("chatId")?.let { chatId ->
            viewModel.setChatId(chatId)
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messageAdapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnSend.setOnClickListener {
                val message = etMessage.text.toString()
                if (message.isNotEmpty()) {
                    viewModel.sendMessage(message)
                    etMessage.text?.clear()
                }
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun observeState() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.submitList(messages)
            binding.rvMessages.scrollToPosition(messages.size - 1)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.toolbar.title = user.name
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 