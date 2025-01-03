package com.modules.db.dataModels

import kotlinx.serialization.Serializable

@Serializable
data class ClassModel(
    val classNbr: String,
    val classTeacherName: String,
)
