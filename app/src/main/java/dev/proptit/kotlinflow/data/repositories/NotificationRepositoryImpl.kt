package dev.proptit.kotlinflow.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dev.proptit.kotlinflow.domain.repositories.INotificationRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseMessaging: FirebaseMessaging
) : INotificationRepository {

    override suspend fun saveFcmToken(userId: String): Result<Unit> {
        return try {
            val token = firebaseMessaging.token.await()
            firestore.collection("users")
                .document(userId)
                .update("fcmToken", token)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFcmToken(userId: String): Result<String> {
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val token = doc.getString("fcmToken")
            if (token != null) {
                Result.success(token)
            } else {
                Result.failure(Exception("FCM token not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFcmToken(userId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .update("fcmToken", null)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendNotification(
        receiverToken: String,
        title: String,
        message: String,
        data: Map<String, String>
    ): Result<Unit> {
        return try {
            // TODO: Implement FCM notification sending
            // This will be implemented using Firebase Cloud Functions
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFCMToken(): String? {
        return try {
            firebaseMessaging.token.await()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun onNewToken(token: String): Result<Unit> {
        return try {
            // TODO: Update token in database
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 