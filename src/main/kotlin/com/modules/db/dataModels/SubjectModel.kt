package com.modules.db.dataModels

import kotlinx.serialization.Serializable

@Serializable
class SubjectModel(
    val index: String,
    val name: String,
    val description: String
)
