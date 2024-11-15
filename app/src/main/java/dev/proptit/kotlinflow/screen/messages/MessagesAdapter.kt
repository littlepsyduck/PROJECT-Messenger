package dev.proptit.kotlinflow.screen.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.proptit.kotlinflow.databinding.ItemMessageBinding
import dev.proptit.kotlinflow.domain.Message

class MessagesAdapter: ListAdapter<Message, MessagesAdapter.MessageVH>(ChatDiffCallback) {
    inner class MessageVH(val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessage.text = message.text
            binding.tvAuthor.text = message.senderId
        }
    }



    object ChatDiffCallback: DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageVH {
        return MessageVH(ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MessageVH, position: Int) {
        holder.bind(getItem(position))
    }
}