package com.modules.db.repos

import com.modules.db.DAO.SubjectsDAO
import com.modules.db.DAO.TeachersDAO
import com.modules.db.dataModels.SubjectModel
import com.modules.db.other.ConstsDB
import com.modules.db.subjectDAOToModel
import com.modules.db.suspendTransaction
import com.modules.db.tables.SubjectsTable
import com.modules.db.tables.TeachersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.update

class SubjectRepo {
    suspend fun getAll(): List<SubjectModel> =
        suspendTransaction {
            SubjectsDAO.all().map(::subjectDAOToModel).sortedBy { it.index }
        }

    suspend fun getByIndex(index: String) =
        suspendTransaction {
            SubjectsDAO
                .find { (SubjectsTable.index eq index) }
                .map(::subjectDAOToModel)
                .firstOrNull()
        }

    suspend fun removeByIndex(index: String) =
        suspendTransaction {
            val subject = SubjectsDAO.find { (SubjectsTable.index eq index) }.firstOrNull()
            if (subject == null || subject.index == ConstsDB.N_A) {
                return@suspendTransaction false
            }

            val allSubjectTeachers = TeachersDAO.find { TeachersTable.subjectIndex eq index }
            allSubjectTeachers.forEach {
                TeachersTable.update({ TeachersTable.index eq it.index }) {
                    it[TeachersTable.subjectIndex] = ConstsDB.N_A
                }
            }
            val rowsDeleted = SubjectsTable.deleteWhere { SubjectsTable.index eq index }

            rowsDeleted == 1
        }

    suspend fun addRow(newRow: SubjectModel): Unit =
        suspendTransaction {
//      These checks ensure that the indexes are unique
            if (SubjectsDAO.find { (SubjectsTable.index eq newRow.index) }.count() > 0) {
                return@suspendTransaction
            }

            SubjectsDAO.new {
                index = newRow.index
                name = newRow.name
                description = newRow.description
            }
        }

    suspend fun updateRow(
        index: String,
        name: String,
        description: String,
    ): Unit =
        suspendTransaction {
            val subject = SubjectsDAO.find { (SubjectsTable.index eq index) }.firstOrNull()

            if (subject == null) {
                return@suspendTransaction
            }

            SubjectsTable.update({ SubjectsTable.index eq index }) {
                it[SubjectsTable.name] = name
                it[SubjectsTable.description] = description
            }

            TransactionManager.current().commit()
        }
}
