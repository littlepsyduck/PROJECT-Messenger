package dev.proptit.kotlinflow

import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.GsonBuilder
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Throws(SerializationException::class, NoSuchElementException::class)
inline fun <reified T> DocumentSnapshot.toObject(): T {
    val gson = GsonBuilder().create()
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    val data: Map<String, Any>? = this.data
    if (data == null) throw NoSuchElementException("DocumentSnapshot is empty")
    val jsonStr = gson.toJson(data)
    return json.decodeFromString(jsonStr)
}