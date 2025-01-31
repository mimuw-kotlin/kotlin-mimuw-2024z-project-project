package com.modules

import com.modules.db.other.PswdCheckRetVal
import com.modules.db.reposInterfaces.PasswordInterface

class FakePswdRepo : PasswordInterface {
    private val passwords =
        mutableMapOf(
            "admin" to "admin",
            "teacher" to "teacher",
            "student1" to "student1",
            "student2" to "student2",
        )

    override suspend fun checkPassword(
        username: String,
        password: String,
    ): PswdCheckRetVal {
        return if (passwords[username] == password) {
            PswdCheckRetVal.PASSWORD_CORRECT
        } else {
            PswdCheckRetVal.PASSWORD_INCORRECT
        }
    }

    override suspend fun setPassword(
        username: String,
        password: String,
    ) {
        if (passwords[username] == null) {
            passwords[username] = password
        }
    }

    override suspend fun updateUsername(
        oldUsername: String,
        newUsername: String,
    ) {
        TODO("Not implemented")
    }
}
