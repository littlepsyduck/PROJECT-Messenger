package dev.proptit.kotlinflow.dto

import dev.proptit.kotlinflow.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val uid: String = "",
    val email: String = "",
    val name: String = ""
) {
    constructor() : this("", "", "")
    fun toUser(id: String) = User(id, email, name)
}