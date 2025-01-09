package com.modules

import com.modules.db.dataModels.TeacherModel
import com.modules.db.reposInterfaces.SchoolUsersInterface

class FakeTeacherRepo : SchoolUsersInterface<TeacherModel> {

    private val teachers = mutableMapOf(
        "teacher" to TeacherModel("21372137", "teacher", "teacher", "1E", "N/A", true),
    )

    override suspend fun getAll(): List<TeacherModel> {
        return teachers.values.toList()
    }

    override suspend fun getByIndex(index: String): TeacherModel? {
        return teachers.values.find { it.index == index }
    }

    override suspend fun removeByIndex(index: String): Boolean {
        val teacher = teachers.values.find { it.index == index }
        return if (teacher != null) {
            teachers.remove(teacher.username)
            true
        } else {
            false
        }
    }

    override suspend fun addRow(newRow: TeacherModel) : Unit {
        if (teachers[newRow.username] == null) {
            teachers[newRow.username] = newRow
        }
    }

    override suspend fun getByClassNbr(clsNbr: String): List<TeacherModel> {
        return teachers.values.filter { it.classNbr == clsNbr }
    }

    override suspend fun getByUsername(username: String) : TeacherModel?{
        return teachers[username]
    }

    override suspend fun updateRow(
        index: String,
        username: String,
        userType: String,
        classNbr: String,
        subjectIndex: String,
        active: Boolean,) : Unit {
        val teacher = teachers.values.find { it.index == index }
        if (teacher != null) {
            teachers[teacher.username] = TeacherModel(index, username, userType, classNbr, subjectIndex, active)
        }
    }

    override suspend fun toggleActiveByIndex(index: String): Boolean {
        val teacher = teachers.values.find { it.index == index }
        return if (teacher != null) {
            teachers[teacher.username] = teacher.copy(active = !teacher.active)
            true
        } else {
            false
        }
    }
}