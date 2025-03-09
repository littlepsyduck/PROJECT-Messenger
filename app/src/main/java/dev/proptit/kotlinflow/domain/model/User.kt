package dev.proptit.kotlinflow.domain.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val avatar: String? = null,
    val status: UserStatus = UserStatus.OFFLINE,
    val fcmToken: String? = null
)

enum class UserStatus {
    ONLINE,
    OFFLINE
} 