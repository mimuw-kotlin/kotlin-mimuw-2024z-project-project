package com.modules.db.repos

import com.modules.db.dataModels.StudentModel
import com.modules.db.tables.StudentsTable
import com.modules.db.DAO.StudentsDAO
import com.modules.db.DAO.SubjectsDAO
import com.modules.db.DAO.TeachersDAO
import com.modules.db.dataModels.SubjectModel
import com.modules.db.studentDAOToModel
import com.modules.db.reposInterfaces.SchoolUsersInterface
import com.modules.db.subjectDAOToModel
import com.modules.db.suspendTransaction
import com.modules.db.tables.PasswordsTable
import com.modules.db.tables.SubjectsTable
import com.modules.db.tables.TeachersTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.update

class SubjectRepo {

    suspend fun getByName(subjectName: String): SubjectModel? = suspendTransaction {
        SubjectsDAO
            .find { (SubjectsTable.subjectName eq subjectName) }
            .map(::subjectDAOToModel)
            .firstOrNull()
    }

    suspend fun getAll(): List<SubjectModel> = suspendTransaction {
        SubjectsDAO.all().map(::subjectDAOToModel).sortedBy { it.subjectIndex }
    }

    suspend fun getByIndex(index: String) = suspendTransaction {
        SubjectsDAO
            .find { (SubjectsTable.subjectIndex eq index) }
            .map(::subjectDAOToModel)
            .firstOrNull()
    }

    suspend fun removeByIndex(index: String) = suspendTransaction {
        val subject = SubjectsDAO.find { (SubjectsTable.subjectIndex eq index) }.firstOrNull()
        if (subject == null)
            return@suspendTransaction false

        val allSubjectTeachers = TeachersDAO.find { TeachersTable.subjectIndex eq index }
        allSubjectTeachers.forEach {
            TeachersTable.update({ TeachersTable.index eq it.index }) {
                it[TeachersTable.subjectIndex] = "N/A"
            }
        }
        val rowsDeleted = SubjectsTable.deleteWhere { SubjectsTable.subjectIndex eq index }

        rowsDeleted == 1
    }

    suspend fun addRow(newRow: SubjectModel): Unit = suspendTransaction {

//      These checks ensure that the indexes are unique
        if (SubjectsDAO.find { (SubjectsTable.subjectIndex eq newRow.subjectIndex) }.count() > 0)
            return@suspendTransaction

        SubjectsDAO.new {
            subjectIndex = newRow.subjectIndex
            subjectName = newRow.subjectName
            description = newRow.description
        }
    }

    suspend fun updateRow(index: String, name: String, description: String): Unit = suspendTransaction {
        val subject = SubjectsDAO.find { (SubjectsTable.subjectIndex eq index) }.firstOrNull()

        if (subject == null)
            return@suspendTransaction

        SubjectsTable.update ({ SubjectsTable.subjectIndex eq index }) {
            it[SubjectsTable.subjectName] = name
            it[SubjectsTable.description] = description
        }

        TransactionManager.current().commit()
    }
}
