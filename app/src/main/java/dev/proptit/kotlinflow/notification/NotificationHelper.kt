package dev.proptit.kotlinflow.notification

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

@SuppressLint("StaticFieldLeak")
object NotificationHelper {
    private const val TAG = "NotificationHelper"
    private const val FCM_API_URL = "https://fcm.googleapis.com/v1/projects/practiceflow-fcf35/messages:send"
    private var accessToken: String? = null
    private val client = OkHttpClient()
    private lateinit var context: Context

    fun initialize(appContext: Context) {
        context = appContext.applicationContext
    }

    private suspend fun getAccessToken(): String {
        if (accessToken != null) {
            return accessToken!!
        }

        return withContext(Dispatchers.IO) {
            try {
                val stream = context.assets.open("service-account.json")
                val credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
                credentials.refresh()
                accessToken = credentials.accessToken.tokenValue
                Log.d(TAG, "Got new access token")
                accessToken!!
            } catch (e: IOException) {
                Log.e(TAG, "Error getting access token", e)
                throw e
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    suspend fun sendNotification(
        token: String,
        title: String,
        message: String,
        data: Map<String, String>
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val jsonBody = JSONObject().apply {
                    put("message", JSONObject().apply {
                        put("token", token)
                        put("notification", JSONObject().apply {
                            put("title", title)
                            put("body", message)
                        })
                        put("data", JSONObject().apply {
                            data.forEach { (key, value) ->
                                put(key, value)
                            }
                        })
                    })
                }

                val request = Request.Builder()
                    .url(FCM_API_URL)
                    .addHeader("Authorization", "Bearer ${getAccessToken()}")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    val success = response.isSuccessful
                    if (success) {
                        Log.d(TAG, "Notification sent successfully to token: $token")
                    } else {
                        Log.e(TAG, "Failed to send notification: ${response.body?.string()}")
                    }
                    success
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending notification", e)
                false
            }
        }
    }
} 