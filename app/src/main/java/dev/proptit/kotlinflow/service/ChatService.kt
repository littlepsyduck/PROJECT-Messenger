package dev.proptit.kotlinflow.service

import dev.proptit.kotlinflow.domain.Chat

class ChatService() {
//    private val firebase = Firebase.firestore
    suspend fun getUserChats(uid: String): List<Chat> {
        return listOf(
            Chat(
                "c1",
                "chat 1",
            )
        )
    }
}