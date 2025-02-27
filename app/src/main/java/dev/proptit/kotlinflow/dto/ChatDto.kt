package dev.proptit.kotlinflow.dto

import dev.proptit.kotlinflow.domain.Chat
import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    val title: String = "",  // 🔹 Thêm giá trị mặc định
    val participants: List<String> = emptyList(),
    val avatar: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: Long? = null
) {
    fun mapToChat(id: String): Chat {
        return Chat(
            id = id,
            title = title,
            avatar = avatar,
            lastMessage = lastMessage,
            lastMessageTime = lastMessageTime
        )
    }
}

