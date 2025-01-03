package com.modules.db.repos

import com.modules.db.dataModels.StudentModel
import com.modules.db.tables.StudentsTable
import com.modules.db.DAO.StudentsDAO
import com.modules.db.studentDAOToModel
import com.modules.db.reposInterfaces.SchoolUsersInterface
import com.modules.db.suspendTransaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class StudentRepo : SchoolUsersInterface<StudentModel>{

    override suspend fun getByClassNbr(clsNbr: String): List<StudentModel> = suspendTransaction {
        StudentsDAO
            .find {(StudentsTable.classNbr eq clsNbr)}
            .map(::studentDAOToModel)
    }

    override suspend fun getByUsername(username: String): StudentModel? = suspendTransaction {
        StudentsDAO
            .find {(StudentsTable.username eq username)}
            .map(::studentDAOToModel)
            .firstOrNull()
    }

    override suspend fun getAll(): List<StudentModel> = suspendTransaction {
        StudentsDAO.all().map(::studentDAOToModel)
    }

    override suspend fun getByIndex(index: String) = suspendTransaction {
        StudentsDAO
            .find {(StudentsTable.index eq index)}
            .map(::studentDAOToModel)
            .firstOrNull()
    }

    override suspend fun removeByIndex(index: String) = suspendTransaction {
        val rowsDeleted = StudentsTable.deleteWhere { StudentsTable.index eq index }
        rowsDeleted == 1
    }

    override suspend fun addRow(newRow: StudentModel): Unit = suspendTransaction {

        // Here we should throw exception - TODO
        if (StudentsDAO.find {(StudentsTable.index eq newRow.index)}.count() > 0)
            return@suspendTransaction

        StudentsDAO.new {
            index = newRow.index
            username = newRow.username
            userType = newRow.userType
            classNbr = newRow.classNbr
        }
    }
}