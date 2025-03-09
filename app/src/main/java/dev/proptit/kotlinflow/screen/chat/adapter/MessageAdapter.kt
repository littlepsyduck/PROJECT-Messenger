package dev.proptit.kotlinflow.screen.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.proptit.kotlinflow.R
import dev.proptit.kotlinflow.domain.model.Message
import dev.proptit.kotlinflow.domain.model.MessageStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val currentUserId: String) : 
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<Message>()
    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    fun updateMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                SentMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount() = messages.size

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(message: Message) {
            tvMessage.text = message.content
            tvTime.text = formatTime(message.timestamp)
            tvStatus.text = when (message.status) {
                MessageStatus.SENT -> "✓"
                MessageStatus.DELIVERED -> "✓✓"
                MessageStatus.READ -> "✓✓" // Có thể thay bằng icon khác
            }
            tvStatus.setTextColor(
                itemView.context.getColor(
                    when (message.status) {
                        MessageStatus.READ -> android.R.color.holo_blue_light
                        else -> android.R.color.darker_gray
                    }
                )
            )
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: Message) {
            tvMessage.text = message.content
            tvTime.text = formatTime(message.timestamp)
        }
    }

    private fun formatTime(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
} 