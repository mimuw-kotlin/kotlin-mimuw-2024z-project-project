package com.modules.db.repos

import com.modules.db.DAO.StudentsDAO
import com.modules.db.DAO.TeachersDAO
import com.modules.db.dataModels.StudentModel
import com.modules.db.reposInterfaces.SchoolUsersInterface
import com.modules.db.studentDAOToModel
import com.modules.db.suspendTransaction
import com.modules.db.tables.PasswordsTable
import com.modules.db.tables.StudentsTable
import com.modules.db.tables.TeachersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.update

class StudentRepo : SchoolUsersInterface<StudentModel> {

    override suspend fun getAll(): List<StudentModel> =
        suspendTransaction {
            StudentsDAO.all().map(::studentDAOToModel).sortedBy { it.index }
        }

    override suspend fun getByIndex(index: String) =
        suspendTransaction {
            StudentsDAO
                .find { (StudentsTable.index eq index) }
                .map(::studentDAOToModel)
                .firstOrNull()
        }

    override suspend fun removeByIndex(index: String) =
        suspendTransaction {
            val student = StudentsDAO.find { (StudentsTable.index eq index) }.firstOrNull()
            if (student == null) {
                return@suspendTransaction false
            }

            val pswdDeleted = PasswordsTable.deleteWhere { username eq student.username }
            val rowsDeleted = StudentsTable.deleteWhere { StudentsTable.index eq index }

            rowsDeleted == 1 && pswdDeleted == 1
        }

    override suspend fun addRow(newRow: StudentModel): Unit =
        suspendTransaction {
//      These checks ensure that the indexes are unique

            if (StudentsDAO.find { (StudentsTable.index eq newRow.index) }.count() > 0 ||
                StudentsDAO.find { (StudentsTable.username eq newRow.username) }.count() > 0
            ) {
                return@suspendTransaction
            }

            if (TeachersDAO.find { (TeachersTable.index eq newRow.index) }.count() > 0 ||
                TeachersDAO.find { (TeachersTable.username eq newRow.username) }.count() > 0
            ) {
                return@suspendTransaction
            }

            StudentsDAO.new {
                index = newRow.index
                username = newRow.username
                userType = newRow.userType
                classNbr = newRow.classNbr
            }
        }

    override suspend fun getByClassNbr(clsNbr: String): List<StudentModel> =
        suspendTransaction {
            StudentsDAO
                .find { (StudentsTable.classNbr eq clsNbr) }
                .map(::studentDAOToModel).sortedBy { it.username }
        }

    override suspend fun getByUsername(username: String): StudentModel? =
        suspendTransaction {
            StudentsDAO
                .find { (StudentsTable.username eq username) }
                .map(::studentDAOToModel)
                .firstOrNull()
        }

    override suspend fun updateRow(
        index: String,
        username: String,
        userType: String,
        classNbr: String,
        subjectIndex: String,
        active: Boolean,
    ): Unit =
        suspendTransaction {
            val updatedRow = StudentModel(index, username, userType, classNbr, active)
            val student = StudentsDAO.find { (StudentsTable.index eq updatedRow.index) }.firstOrNull()

            if (student == null) {
                return@suspendTransaction
            }

            StudentsTable.update({ StudentsTable.index eq updatedRow.index }) {
                it[StudentsTable.username] = updatedRow.username
                it[StudentsTable.userType] = updatedRow.userType
                it[StudentsTable.classNbr] = updatedRow.classNbr
                it[StudentsTable.active] = updatedRow.active
            }

//          This is needed so that we don't get old data from the database
            TransactionManager.current().commit()
        }

    override suspend fun toggleActiveByIndex(index: String): Boolean =
        suspendTransaction {
            val student = StudentsDAO.find { (StudentsTable.index eq index) }.firstOrNull()
            if (student == null) {
                return@suspendTransaction false
            }

            StudentsTable.update({ StudentsTable.index eq index }) {
                it[StudentsTable.active] = !student.active
            }

            TransactionManager.current().commit()
            true
        }
}
