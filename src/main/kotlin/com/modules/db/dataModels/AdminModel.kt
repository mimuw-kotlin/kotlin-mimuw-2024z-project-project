package com.modules.db.dataModels
import kotlinx.serialization.Serializable

@Serializable
data class AdminModel(
    val username: String,
    val userType: String
)
