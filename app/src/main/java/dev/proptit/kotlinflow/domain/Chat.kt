package dev.proptit.kotlinflow.domain

data class Chat(
    val id: String,
    val title: String,
    val avatar: String?, // Link ảnh đại diện
    val lastMessage: String?, // Tin nhắn gần nhất
    val lastMessageTime: Long? // Thời gian tin nhắn gần nhất
)
