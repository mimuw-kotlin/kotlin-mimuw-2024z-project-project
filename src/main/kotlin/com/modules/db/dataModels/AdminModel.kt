package com.modules.db.dataModels
import kotlinx.serialization.Serializable

@Serializable
data class AdminModel(
    val username: String,
    val password: String,
    val userType: String
)
