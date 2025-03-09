package dev.proptit.kotlinflow.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Parcelize
data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val status: MessageStatus = MessageStatus.SENT
) : Parcelable

enum class MessageStatus {
    SENT,
    DELIVERED,
    READ
}