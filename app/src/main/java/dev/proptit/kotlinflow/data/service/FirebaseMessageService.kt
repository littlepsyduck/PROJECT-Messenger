package dev.proptit.kotlinflow.data.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dev.proptit.kotlinflow.MainActivity
import dev.proptit.kotlinflow.R
import dev.proptit.kotlinflow.data.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessageService : FirebaseMessagingService() {
    private val TAG = "FirebaseMessageService"
    private val scope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        
        scope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val database = FirebaseDatabase.getInstance().reference
                    database.child("users").child(userId).child("fcmToken").setValue(token).await()
                    Log.d(TAG, "FCM token updated for user: $userId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating FCM token", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received: ${remoteMessage.data}")

        // Kiểm tra xem người dùng hiện tại có phải là người nhận không
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val receiverId = remoteMessage.data["receiverId"]
        if (currentUserId != receiverId) {
            Log.d(TAG, "Message not for current user")
            return
        }

        // Lấy thông tin tin nhắn
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        val chatId = remoteMessage.data["chatId"]
        val senderId = remoteMessage.data["senderId"]

        // Hiển thị notification ngay lập tức
        showNotification(
            title ?: "Tin nhắn mới",
            message ?: "Bạn có một tin nhắn mới",
            chatId,
            senderId
        )
    }

    private fun showNotification(
        title: String,
        message: String,
        chatId: String?,
        senderId: String?
    ) {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Tạo intent để mở ứng dụng khi click vào notification
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("chatId", chatId)
                putExtra("senderId", senderId)
            }

            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Âm thanh thông báo
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            // Xây dựng notification
            val notification = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setVibrate(longArrayOf(0, 1000, 500, 1000))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()

            // Hiển thị notification
            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notification)
            Log.d(TAG, "Notification shown with id: $notificationId")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }
} 