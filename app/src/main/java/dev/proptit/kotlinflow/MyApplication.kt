package dev.proptit.kotlinflow

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import dev.proptit.kotlinflow.domain.usecases.auth.UpdateFCMTokenUseCase

@HiltAndroidApp
class MyApplication : Application() {
    private val TAG = "MyApplication"
    private val scope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var updateFCMTokenUseCase: UpdateFCMTokenUseCase

    companion object {
        const val CHANNEL_NAME = "Chat Messages"
        const val CHANNEL_DESCRIPTION = "Thông báo tin nhắn chat"
    }

    override fun onCreate() {
        super.onCreate()
        
        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this)
        
        // Tạo notification channel
        createNotificationChannel()
        
        // Lấy FCM token
        getFCMToken()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }
    
    private fun getFCMToken() {
        scope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d(TAG, "FCM Token: $token")
                updateFCMTokenUseCase(token)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting FCM token", e)
            }
        }
    }
} 