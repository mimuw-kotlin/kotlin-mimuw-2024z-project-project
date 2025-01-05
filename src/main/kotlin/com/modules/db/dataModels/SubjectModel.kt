package com.modules.db.dataModels

import kotlinx.serialization.Serializable

@Serializable
class SubjectModel(
    val subjectIndex: String,
    val subjectName: String,
    val description: String
)
