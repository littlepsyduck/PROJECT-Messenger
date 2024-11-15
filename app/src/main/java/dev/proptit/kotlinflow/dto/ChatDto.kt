package dev.proptit.kotlinflow.dto

import dev.proptit.kotlinflow.domain.Chat
import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    val title: String,
) {
    fun mapToChat(id: String): Chat {
        return Chat(
            id = id,
            title = title,
        )
    }
}