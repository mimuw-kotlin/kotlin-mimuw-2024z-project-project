package com.modules.db.repos

import com.modules.db.adminDAOToModel
import com.modules.db.dao.AdminDAO
import com.modules.db.dataModels.AdminModel
import com.modules.db.other.ConstsDB
import com.modules.db.reposInterfaces.AdminInterface
import com.modules.db.suspendTransaction
import com.modules.db.tables.AdminTable
import com.modules.db.tables.PasswordsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class AdminRepo : AdminInterface {
    override suspend fun getByUsername(username: String): AdminModel? =
        suspendTransaction {
            AdminDAO
                .find { (AdminTable.username eq username) }
                .map(::adminDAOToModel)
                .firstOrNull()
        }

    override suspend fun removeByUsername(username: String) =
        suspendTransaction {
            if (username != ConstsDB.SUPER_ADMIN) {
                val adminDeleted = AdminTable.deleteWhere { AdminTable.username eq username }
                val pswdDeleted = PasswordsTable.deleteWhere { PasswordsTable.username eq username }

                adminDeleted == 1 && pswdDeleted == 1
            } else {
                false
            }
        }

    override suspend fun addRow(newRow: AdminModel) =
        suspendTransaction {
            val isPresent = AdminDAO.find { AdminTable.username eq newRow.username }.firstOrNull()
            if (isPresent == null) {
                AdminDAO.new {
                    username = newRow.username
                    userType = newRow.userType
                }
                return@suspendTransaction true
            } else {
                return@suspendTransaction false
            }
        }
}
