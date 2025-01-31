package com.modules.db.repos

import com.modules.db.classDAOToModel
import com.modules.db.dao.ClassesDAO
import com.modules.db.dao.TeachersDAO
import com.modules.db.dataModels.ClassModel
import com.modules.db.dataModels.TeacherModel
import com.modules.db.suspendTransaction
import com.modules.db.tables.ClassesTable
import com.modules.db.tables.TeachersTable
import com.modules.db.teacherDAOToModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.update

class ClassRepo {
    suspend fun getAll(): List<ClassModel> =
        suspendTransaction {
            ClassesDAO.all().map(::classDAOToModel)
        }

    suspend fun getByClassNbr(classNbr: String) =
        suspendTransaction {
            ClassesDAO
                .find { (ClassesTable.classNbr eq classNbr) }
                .map(::classDAOToModel)
                .firstOrNull()
        }

    suspend fun removeByClassNbr(classNbr: String) =
        suspendTransaction {
            val cls = ClassesDAO.find { (ClassesTable.classNbr eq classNbr) }.firstOrNull()
            if (cls == null) {
                return@suspendTransaction false
            }

            val rowsDeleted = ClassesTable.deleteWhere { ClassesTable.classNbr eq classNbr }

            rowsDeleted == 1
        }

    suspend fun addRow(newRow: ClassModel): Unit =
        suspendTransaction {
            if (ClassesDAO.find { (ClassesTable.classNbr eq newRow.classNbr) }.count() > 0) {
                return@suspendTransaction
            }

            ClassesDAO.new {
                classNbr = newRow.classNbr
                classTeacherName = newRow.classTeacherName
            }
        }

    suspend fun updateRow(
        classNbr: String,
        classTeacherName: String?,
    ): Unit =
        suspendTransaction {
            val updatedRow = ClassModel(classNbr, classTeacherName)
            val cls = ClassesDAO.find { (ClassesTable.classNbr eq updatedRow.classNbr) }.firstOrNull()

            if (cls == null) {
                return@suspendTransaction
            }

            ClassesTable.update({ ClassesTable.classNbr eq updatedRow.classNbr }) {
                it[ClassesTable.classNbr] = updatedRow.classNbr
                it[ClassesTable.classTeacherName] = updatedRow.classTeacherName
            }

//      This is needed so that we don't get old data from the database
            TransactionManager.current().commit()
        }

    suspend fun getTeacherFromClass(classNbr: String): TeacherModel? =
        suspendTransaction {
            val cls = ClassesDAO.find { (ClassesTable.classNbr eq classNbr) }.firstOrNull()
            if (cls == null) {
                return@suspendTransaction null
            }

            val clsTeacherName = cls.classTeacherName ?: return@suspendTransaction null

            val teacher = TeachersDAO.find { TeachersTable.username eq clsTeacherName }.firstOrNull()

            if (teacher == null) {
                return@suspendTransaction null
            }

            return@suspendTransaction teacherDAOToModel(teacher)
        }
}
