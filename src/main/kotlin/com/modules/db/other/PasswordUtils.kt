package com.modules.db.other
import org.mindrot.jbcrypt.BCrypt

object PasswordUtils {
    fun hashPassword(passwordFromUserInput: String): Pair<String, String> {
        val salt = BCrypt.gensalt()
        return Pair(BCrypt.hashpw(passwordFromUserInput, salt), salt)
    }

    fun verifyPassword(passwordFromUserInput: String,
                       hashedPassword: String): Boolean {
        return BCrypt.checkpw(passwordFromUserInput, hashedPassword)
    }
}
