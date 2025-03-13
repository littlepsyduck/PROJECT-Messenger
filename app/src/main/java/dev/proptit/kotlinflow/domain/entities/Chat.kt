package dev.proptit.kotlinflow.domain.entities

data class Chat(
    var id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: Message? = null,
    val timestamp: Long = 0,
    val unreadCount: Int = 0
) 