package com.modules.db.dataModels

import kotlinx.serialization.Serializable

@Serializable
class PasswordModel (
    var username: String,
    var password: String
)