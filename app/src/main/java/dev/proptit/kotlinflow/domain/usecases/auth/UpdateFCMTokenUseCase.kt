package dev.proptit.kotlinflow.domain.usecases.auth

import dev.proptit.kotlinflow.domain.repositories.IAuthRepository
import javax.inject.Inject

class UpdateFCMTokenUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        return authRepository.updateFCMToken(token)
    }
} 