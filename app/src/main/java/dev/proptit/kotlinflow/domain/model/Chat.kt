package dev.proptit.kotlinflow.domain.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
    val id: String = "",
    val participants: List<String> = listOf(), // List of user IDs
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) 