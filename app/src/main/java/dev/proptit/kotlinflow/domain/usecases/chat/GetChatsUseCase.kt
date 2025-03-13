package dev.proptit.kotlinflow.domain.usecases.chat

import dev.proptit.kotlinflow.domain.entities.Chat
import dev.proptit.kotlinflow.domain.repositories.IChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val chatRepository: IChatRepository
) {
    operator fun invoke(userId: String): Flow<List<Chat>> {
        return chatRepository.getChats(userId)
    }
} 