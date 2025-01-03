package com.modules.db.repos

import com.modules.db.DAO.PasswordsDAO
import com.modules.db.other.PasswordUtils
import com.modules.db.other.PswdCheckRetVal
import com.modules.db.passwordDAOToModel
import com.modules.db.reposInterfaces.PasswordInterface
import com.modules.db.suspendTransaction
import com.modules.db.tables.PasswordsTable

class PasswordRepo : PasswordInterface {
    override suspend fun checkPassword(username: String, password: String): PswdCheckRetVal = suspendTransaction{

        val details = PasswordsDAO
            .find { (PasswordsTable.username eq username) }
            .map(::passwordDAOToModel)
            .firstOrNull()

        if (details == null)
            return@suspendTransaction PswdCheckRetVal.USER_NOT_FOUND

        val retCheckVal = PasswordUtils.verifyPassword(
                                                passwordFromUserInput = password,
                                                hashedPassword = details.password)

        if (retCheckVal)
            return@suspendTransaction PswdCheckRetVal.PASSWORD_CORRECT

        return@suspendTransaction PswdCheckRetVal.PASSWORD_INCORRECT
    }

    override suspend fun setPassword(username: String, password: String) = suspendTransaction{
        val hashedPasswordWithSalt = PasswordUtils.hashPassword(password)
        val user = PasswordsDAO.new {
            this.username = username
            this.password = hashedPasswordWithSalt.first
        }
    }
}