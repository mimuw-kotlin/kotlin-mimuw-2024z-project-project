package com.modules.db.dataModels

import kotlinx.serialization.Serializable

@Serializable
data class StudentModel(
    val index: String,
    val username: String,
    val userType: String,
    val classNbr: String,
    val active: Boolean = false
)
