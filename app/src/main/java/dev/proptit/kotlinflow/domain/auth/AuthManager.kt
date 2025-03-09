package dev.proptit.kotlinflow.domain.auth

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import dev.proptit.kotlinflow.domain.model.User
import dev.proptit.kotlinflow.domain.model.UserStatus
import kotlinx.coroutines.tasks.await
import com.google.firebase.messaging.FirebaseMessaging

class AuthManager(private val activity: Activity) {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val TAG = "AuthManager"

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "Attempting login for email: $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Login failed: User is null")
            
            // Cập nhật FCM token
            updateFCMToken(user.uid)
            
            Log.d(TAG, "Login successful for user: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            val errorMessage = when {
                e.message?.contains("network") == true -> "Lỗi kết nối mạng. Vui lòng kiểm tra lại kết nối internet."
                e.message?.contains("password") == true -> "Sai mật khẩu."
                e.message?.contains("user") == true -> "Email không tồn tại."
                else -> "Đăng nhập thất bại: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun register(email: String, password: String, name: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "Attempting registration for email: $email")
            
            // Tạo tài khoản
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Registration failed: User is null")
            
            Log.d(TAG, "Account created successfully for user: ${user.uid}")
            
            // Lấy FCM token
            val fcmToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get FCM token", e)
                null
            }
            
            // Tạo profile trong database
            val userProfile = User(
                id = user.uid,
                email = email,
                name = name,
                status = UserStatus.OFFLINE,
                fcmToken = fcmToken
            )
            
            try {
                database.child("users").child(user.uid).setValue(userProfile).await()
                Log.d(TAG, "User profile created successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create user profile", e)
                // Nếu không tạo được profile, xóa tài khoản đã tạo
                user.delete().await()
                throw Exception("Không thể tạo thông tin người dùng: ${e.message}")
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            val errorMessage = when {
                e.message?.contains("network") == true -> "Lỗi kết nối mạng. Vui lòng kiểm tra lại kết nối internet."
                e.message?.contains("email") == true -> "Email đã được sử dụng."
                e.message?.contains("password") == true -> "Mật khẩu phải có ít nhất 6 ký tự."
                else -> "Đăng ký thất bại: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun updateUserStatus(status: UserStatus) {
        try {
            getCurrentUser()?.let { user ->
                database.child("users").child(user.uid).child("status")
                    .setValue(status).await()
                Log.d(TAG, "User status updated to: $status")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update user status", e)
        }
    }

    fun signOut() {
        auth.signOut()
        Log.d(TAG, "User signed out")
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            Log.d(TAG, "Attempting password reset for email: $email")
            auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Password reset email sent successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Password reset failed", e)
            val errorMessage = when {
                e.message?.contains("network") == true -> "Lỗi kết nối mạng. Vui lòng kiểm tra lại kết nối internet."
                e.message?.contains("user") == true -> "Email không tồn tại."
                else -> "Không thể gửi email đặt lại mật khẩu: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    private suspend fun updateFCMToken(userId: String) {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            database.child("users").child(userId).child("fcmToken").setValue(token).await()
            Log.d(TAG, "FCM token updated for user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update FCM token", e)
        }
    }
} 