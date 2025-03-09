package dev.proptit.kotlinflow.screen.chat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.proptit.kotlinflow.R
import dev.proptit.kotlinflow.domain.model.Chat

class ChatAdapter(
    private val onChatClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val chats = mutableListOf<Chat>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateChats(newChats: List<Chat>) {
        chats.clear()
        chats.addAll(newChats)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount() = chats.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvLastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onChatClick(chats[position])
                }
            }
        }

        fun bind(chat: Chat) {
            // TODO: Get user name from participants
            tvName.text = "User Name"
            tvLastMessage.text = chat.lastMessage?.content ?: "No messages"
            tvTime.text = "Time" // TODO: Format timestamp
        }
    }
} 