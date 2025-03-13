package dev.proptit.kotlinflow.domain.usecases.chat

import dev.proptit.kotlinflow.domain.entities.Message
import dev.proptit.kotlinflow.domain.repositories.IChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val chatRepository: IChatRepository
) {
    operator fun invoke(chatId: String): Flow<List<Message>> {
        return chatRepository.getMessages(chatId)
    }
} 