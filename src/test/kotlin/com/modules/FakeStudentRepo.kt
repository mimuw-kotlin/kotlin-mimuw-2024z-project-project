package com.modules

import com.modules.db.dataModels.StudentModel
import com.modules.db.reposInterfaces.SchoolUsersInterface

class FakeStudentRepo : SchoolUsersInterface<StudentModel> {
    private val students =
        mutableMapOf(
            "student1" to
                StudentModel(
                    "12345678",
                    "student1",
                    "student",
                    "1E",
                    true,
                ),
            "student2" to
                StudentModel(
                    "12345679",
                    "student2",
                    "student",
                    "1E",
                    false,
                ),
        )

    override suspend fun getAll(): List<StudentModel> {
        return students.values.toList()
    }

    override suspend fun getByIndex(index: String): StudentModel? {
        return students.values.find { it.index == index }
    }

    override suspend fun removeByIndex(index: String): Boolean {
        val student = students.values.find { it.index == index }
        return if (student != null) {
            students.remove(student.username)
            true
        } else {
            false
        }
    }

    override suspend fun addRow(newRow: StudentModel) {
        if (students[newRow.username] == null) {
            students[newRow.username] = newRow
        }
    }

    override suspend fun getByClassNbr(clsNbr: String): List<StudentModel> {
        return students.values.filter { it.classNbr == clsNbr }
    }

    override suspend fun getByUsername(username: String): StudentModel? {
        return students[username]
    }

    override suspend fun updateRow(
        index: String,
        username: String,
        userType: String,
        classNbr: String,
        subjectIndex: String,
        active: Boolean,
    ) {
        val student = students.values.find { it.index == index }
        if (student != null) {
            students[student.username] = StudentModel(index, username, userType, classNbr, active)
        }
    }

    override suspend fun toggleActiveByIndex(index: String): Boolean {
        val student = students.values.find { it.index == index }
        return if (student != null) {
            students[student.username] = student.copy(active = !student.active)
            true
        } else {
            false
        }
    }
}
