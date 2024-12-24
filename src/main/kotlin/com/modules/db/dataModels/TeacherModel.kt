package com.modules.db.dataModels

import kotlinx.serialization.Serializable

@Serializable
data class TeacherModel(
    val index: String,
    val username: String,
    val user_type: String,
    val class_nbr: String,
    val active: Boolean = false
)
