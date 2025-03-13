package dev.proptit.kotlinflow.domain.entities

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val fcmToken: String? = null,
    val status: UserStatus = UserStatus.OFFLINE
)

enum class UserStatus {
    ONLINE,
    OFFLINE
} 