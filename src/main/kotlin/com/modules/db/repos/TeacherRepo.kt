package com.modules.db.repos

import com.modules.db.DAO.TeachersDAO
import com.modules.db.dataModels.TeacherModel
import com.modules.db.reposInterfaces.SchoolUsersInterface
import com.modules.db.suspendTransaction
import com.modules.db.tables.TeachersTable
import com.modules.db.teacherDAOToModel
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TeacherRepo : SchoolUsersInterface<TeacherModel>{

    override suspend fun getByClassNbr(clsNbr: String): List<TeacherModel> = suspendTransaction {
        TeachersDAO
            .find {(TeachersTable.classNbr eq clsNbr)}
            .map(::teacherDAOToModel)
    }

    override suspend fun getByUsername(username: String): TeacherModel? = suspendTransaction {
        TeachersDAO
            .find {(TeachersTable.username eq username)}
            .map(::teacherDAOToModel)
            .firstOrNull()
    }

    override suspend fun getAll(): List<TeacherModel> = suspendTransaction {
        TeachersDAO.all().map(::teacherDAOToModel)
    }

    override suspend fun getByIndex(index: String) = suspendTransaction {
        TeachersDAO
            .find {(TeachersTable.index eq index)}
            .map(::teacherDAOToModel)
            .firstOrNull()
    }

    override suspend fun removeByIndex(index: String) = suspendTransaction {
        val rowsDeleted = TeachersTable.deleteWhere { TeachersTable.index eq index }
        rowsDeleted == 1
    }

    override suspend fun addRow(newRow: TeacherModel): Unit = suspendTransaction {
        TeachersDAO.new {
            index = newRow.index
            username = newRow.username
            user_type = newRow.user_type
            class_nbr = newRow.class_nbr
        }
    }
}