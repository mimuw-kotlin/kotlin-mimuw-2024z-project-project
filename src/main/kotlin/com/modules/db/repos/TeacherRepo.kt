package com.modules.db.repos

import com.modules.db.dao.StudentsDAO
import com.modules.db.dao.TeachersDAO
import com.modules.db.dataModels.TeacherModel
import com.modules.db.reposInterfaces.SchoolUsersInterface
import com.modules.db.suspendTransaction
import com.modules.db.tables.PasswordsTable
import com.modules.db.tables.StudentsTable
import com.modules.db.tables.TeachersTable
import com.modules.db.teacherDAOToModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.update

class TeacherRepo : SchoolUsersInterface<TeacherModel> {
    override suspend fun getAll(): List<TeacherModel> =
        suspendTransaction {
            TeachersDAO.all().map(::teacherDAOToModel).sortedBy { it.index }
        }

    override suspend fun getByIndex(index: String) =
        suspendTransaction {
            TeachersDAO
                .find { (TeachersTable.index eq index) }
                .map(::teacherDAOToModel)
                .firstOrNull()
        }

    override suspend fun removeByIndex(index: String) =
        suspendTransaction {
            val teacher = TeachersDAO.find { (TeachersTable.index eq index) }.firstOrNull()

            if (teacher == null) {
                return@suspendTransaction false
            }

            val pswdDeleted = PasswordsTable.deleteWhere { PasswordsTable.username eq teacher.username }
            val rowsDeleted = TeachersTable.deleteWhere { TeachersTable.index eq index }

            rowsDeleted == 1 && pswdDeleted == 1
        }

    override suspend fun addRow(newRow: TeacherModel) =
        suspendTransaction {
//      These checks ensure that the indexes are unique
            if (StudentsDAO.find { (StudentsTable.index eq newRow.index) }.count() > 0 ||
                StudentsDAO.find { (StudentsTable.username eq newRow.username) }.count() > 0
            ) {
                return@suspendTransaction false
            }

            if (TeachersDAO.find { (TeachersTable.index eq newRow.index) }.count() > 0 ||
                TeachersDAO.find { (TeachersTable.username eq newRow.username) }.count() > 0
            ) {
                return@suspendTransaction false
            }

            TeachersDAO.new {
                index = newRow.index
                username = newRow.username
                userType = newRow.userType
                classNbr = newRow.classNbr
                subjectIndex = newRow.subjectIndex
                active = newRow.active
            }
            return@suspendTransaction true
        }

    override suspend fun getByClassNbr(clsNbr: String): List<TeacherModel> =
        suspendTransaction {
            TeachersDAO
                .find { (TeachersTable.classNbr eq clsNbr) }
                .map(::teacherDAOToModel).sortedBy { it.username }
        }

    override suspend fun getByUsername(username: String): TeacherModel? =
        suspendTransaction {
            TeachersDAO
                .find { (TeachersTable.username eq username) }
                .map(::teacherDAOToModel)
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
            val updatedRow = TeacherModel(index, username, userType, classNbr, subjectIndex, active)
            val teacher = TeachersDAO.find { (TeachersTable.index eq updatedRow.index) }.firstOrNull()

            if (teacher == null) {
                return@suspendTransaction
            }
            TeachersTable.update({ TeachersTable.index eq updatedRow.index }) {
                it[TeachersTable.username] = updatedRow.username
                it[TeachersTable.userType] = updatedRow.userType
                it[TeachersTable.classNbr] = updatedRow.classNbr
                it[TeachersTable.subjectIndex] = updatedRow.subjectIndex
                it[TeachersTable.active] = updatedRow.active
            }

            //      This is needed so that we don't get old data from the database
            TransactionManager.current().commit()
        }

    override suspend fun toggleActiveByIndex(index: String): Boolean =
        suspendTransaction {
            val teacher = TeachersDAO.find { (TeachersTable.index eq index) }.firstOrNull()

            if (teacher == null) {
                return@suspendTransaction false
            }

            TeachersTable.update({ TeachersTable.index eq index }) {
                it[TeachersTable.active] = !teacher.active
            }

            TransactionManager.current().commit()
            true
        }
}
