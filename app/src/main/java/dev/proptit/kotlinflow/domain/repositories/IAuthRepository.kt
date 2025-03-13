package dev.proptit.kotlinflow.domain.repositories

import dev.proptit.kotlinflow.domain.entities.User
import dev.proptit.kotlinflow.domain.entities.UserStatus

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, name: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
    suspend fun updateUserStatus(userId: String, status: UserStatus): Result<Unit>
    suspend fun updateFCMToken(token: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
} 