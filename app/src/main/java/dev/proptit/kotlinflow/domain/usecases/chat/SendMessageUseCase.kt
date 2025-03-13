package dev.proptit.kotlinflow.domain.usecases.chat

import dev.proptit.kotlinflow.domain.entities.Message
import dev.proptit.kotlinflow.domain.repositories.IChatRepository
import dev.proptit.kotlinflow.domain.repositories.INotificationRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: IChatRepository,
    private val notificationRepository: INotificationRepository
) {
    suspend operator fun invoke(chatId: String, message: Message): Result<Unit> {
        if (message.content.isBlank()) {
            return Result.failure(Exception("Nội dung tin nhắn không được để trống"))
        }
        
        return chatRepository.sendMessage(chatId, message)
            .onSuccess {
                // Gửi thông báo
                notificationRepository.sendNotification(
                    receiverToken = message.receiverId,
                    title = "Tin nhắn mới",
                    message = message.content,
                    data = mapOf(
                        "chatId" to message.id,
                        "senderId" to message.senderId,
                        "type" to "message"
                    )
                )
            }
    }
} 