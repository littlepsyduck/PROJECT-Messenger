package dev.proptit.kotlinflow.presentation.chat.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.proptit.kotlinflow.databinding.ItemMessageBinding
import dev.proptit.kotlinflow.domain.entities.Message
import dev.proptit.kotlinflow.domain.entities.User
import dev.proptit.kotlinflow.domain.repositories.IAuthRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MessageAdapter @Inject constructor(
    private val authRepository: IAuthRepository
) : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MessageViewHolder(
        private val binding: ItemMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.apply {
                tvMessage.text = message.content
                tvTime.text = formatTimestamp(message.timestamp)
                
                // Kiểm tra xem tin nhắn có phải do người dùng hiện tại gửi không
                val currentUser = authRepository.getCurrentUser()
                if (currentUser?.id == message.senderId) {
                    // Tin nhắn gửi đi
                    messageContainer.setBackgroundResource(R.drawable.bg_message_sent)
                    messageContainer.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = android.view.Gravity.END
                    }
                } else {
                    // Tin nhắn nhận được
                    messageContainer.setBackgroundResource(R.drawable.bg_message_received)
                    messageContainer.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = android.view.Gravity.START
                    }
                }
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val date = Date(timestamp)
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            return formatter.format(date)
        }
    }

    private class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
} 