package com.modules.db.reposInterfaces
import com.modules.db.other.PswdCheckRetVal

interface PasswordInterface {
    suspend fun checkPassword(
        username: String,
        password: String,
    ): PswdCheckRetVal

    suspend fun setPassword(
        username: String,
        password: String,
    )
}
