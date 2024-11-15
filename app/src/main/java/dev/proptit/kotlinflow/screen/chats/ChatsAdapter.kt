package dev.proptit.kotlinflow.screen.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.proptit.kotlinflow.databinding.ItemChatBinding
import dev.proptit.kotlinflow.domain.Chat

class ChatsAdapter(private val onChatClick: (Chat) -> Unit): ListAdapter<Chat, ChatsAdapter.ChatVH>(
    ChatDiffCallback
) {
    inner class ChatVH(private val binding: ItemChatBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.tvChatTitle.text = chat.title
            binding.tvLatestMessage.text = "Latest message: null"
            binding.root.setOnClickListener {
                onChatClick(chat)
            }
        }
    }

    object ChatDiffCallback: DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatVH {
        return ChatVH(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChatVH, position: Int) {
        holder.bind(getItem(position))
    }
}