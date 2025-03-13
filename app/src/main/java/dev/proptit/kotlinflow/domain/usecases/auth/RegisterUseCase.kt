package dev.proptit.kotlinflow.domain.usecases.auth

import dev.proptit.kotlinflow.domain.entities.User
import dev.proptit.kotlinflow.domain.repositories.IAuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String
    ): Result<User> {
        return authRepository.register(email, password, name)
    }
} 