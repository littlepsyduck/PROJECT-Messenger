package dev.proptit.kotlinflow.service

import com.google.firebase.firestore.DocumentChange
import dev.proptit.kotlinflow.domain.Message
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageService {
    suspend fun collectMessageUpdate(chatId: String,  onMessageUpdate: suspend (DocumentChange.Type, Message) -> Unit) {
        coroutineScope {
            launch {
                (1..5).forEach {
                    delay(1000)
                    onMessageUpdate(
                        DocumentChange.Type.ADDED,
                        Message(
                            "m1",
                            "u1",
                            "content $it",
                            System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    suspend fun getMessages(chatId: String): List<Message> {
        return (1..10).map {
            Message(
                "m$it",
                "u1",
                "content $it",
                System.currentTimeMillis()
            )
        }
    }
}