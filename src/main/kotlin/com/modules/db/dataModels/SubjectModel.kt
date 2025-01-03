package com.modules.db.dataModels

import kotlinx.serialization.Serializable

@Serializable
class SubjectModel(
    val subjectCode: String,
    val subjectName: String,
    val description: String
)
