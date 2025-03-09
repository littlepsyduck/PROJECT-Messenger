package dev.proptit.kotlinflow.screen.chat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.proptit.kotlinflow.R
import dev.proptit.kotlinflow.domain.model.User
import dev.proptit.kotlinflow.domain.model.UserStatus

class UserAdapter(
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val users = mutableListOf<User>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsers(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onUserClick(users[position])
                }
            }
        }

        fun bind(user: User) {
            tvName.text = user.name
            tvStatus.text = when (user.status) {
                UserStatus.ONLINE -> "Online"
                UserStatus.OFFLINE -> "Offline"
            }
            tvStatus.setTextColor(
                itemView.context.getColor(
                    when (user.status) {
                        UserStatus.ONLINE -> android.R.color.holo_green_dark
                        UserStatus.OFFLINE -> android.R.color.darker_gray
                    }
                )
            )
        }
    }
} 