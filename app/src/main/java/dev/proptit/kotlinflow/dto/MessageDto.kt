package dev.proptit.kotlinflow.dto

import dev.proptit.kotlinflow.domain.Message
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val senderId: String = "",
    val text: String = "",
    val createdAt: Long = 0L
) {
    constructor() : this("", "", 0L)

    fun mapToMessage(id: String): Message {
        return Message(
            senderId = senderId,
            text = text,
            createdAt = createdAt,
            id = id
        )
    }
}
