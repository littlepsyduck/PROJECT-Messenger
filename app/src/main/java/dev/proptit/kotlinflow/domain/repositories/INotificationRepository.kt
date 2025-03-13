package dev.proptit.kotlinflow.domain.repositories

import dev.proptit.kotlinflow.domain.entities.Message

interface INotificationRepository {
    suspend fun sendNotification(
        receiverToken: String,
        title: String,
        message: String,
        data: Map<String, String>
    ): Result<Unit>
    
    suspend fun getFCMToken(): String?
    suspend fun onNewToken(token: String): Result<Unit>

    suspend fun saveFcmToken(userId: String): Result<Unit>
    suspend fun getFcmToken(userId: String): Result<String>
    suspend fun deleteFcmToken(userId: String): Result<Unit>
} 