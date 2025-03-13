package dev.proptit.kotlinflow.presentation.chat.chatlist

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
import dev.proptit.kotlinflow.R
import dev.proptit.kotlinflow.databinding.FragmentChatListBinding
import dev.proptit.kotlinflow.presentation.chat.chatlist.adapter.ChatListAdapter

@AndroidEntryPoint
class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatListViewModel by viewModels()
    private lateinit var chatListAdapter: ChatListAdapter

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
        setupRecyclerView()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        chatListAdapter = ChatListAdapter { chat ->
            val bundle = Bundle().apply {
                putString("chatId", chat.id)
                putString("senderId", chat.participants.firstOrNull()?.id)
            }
            findNavController().navigate(R.id.action_chatListFragment_to_chatFragment, bundle)
        }

        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatListAdapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_logout -> {
                        viewModel.logout()
                        findNavController().navigate(R.id.action_chatListFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun observeState() {
        viewModel.chats.observe(viewLifecycleOwner) { chats ->
            chatListAdapter.submitList(chats)
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.toolbar.title = "Xin chào, ${user.name}"
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 