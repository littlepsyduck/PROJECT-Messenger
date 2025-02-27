package dev.proptit.kotlinflow.domain

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val senderId: String,
    val senderName: String = "Unknown",
    val avatarUrl: String = "",
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
)
