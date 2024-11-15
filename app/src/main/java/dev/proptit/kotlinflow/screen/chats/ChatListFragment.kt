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
import kotlinx.coroutines.launch

class ChatListFragment: Fragment() {
    private val vimel by viewModels<ChatListVimel>()
    private val binding by lazy {
        FragmentChatListBinding.inflate(LayoutInflater.from(requireContext()))
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

        binding.rvChatList.adapter = ChatsAdapter {
            findNavController().navigate(R.id.action_chatListFragment_to_chatFragment, args = bundleOf(
                "chatId" to it.id
            ))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                vimel.data.collect {
                    (binding.rvChatList.adapter as ChatsAdapter).submitList(it)
                }
            }
        }
    }
}