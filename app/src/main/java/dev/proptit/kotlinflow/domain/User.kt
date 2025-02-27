package dev.proptit.kotlinflow.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

data class User(
    val id: String,
    val email: String,
    val name: String
)
