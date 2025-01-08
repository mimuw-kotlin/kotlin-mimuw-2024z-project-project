package com.modules.db.other

// Ultimately there will be a sql table for user types
object UserTypes {
    private val allowedTypes =
        mutableSetOf(
            ConstsDB.STUDENT,
            ConstsDB.TEACHER,
            ConstsDB.HEADMASTER,
            ConstsDB.ADMIN,
        )

    fun getType(type: String): String {
        if (type.lowercase() in allowedTypes) {
            return type.lowercase()
        }
        throw Exception("User type not allowed")
    }

    fun getStudentType(): String {
        return ConstsDB.STUDENT.lowercase()
    }

    fun getTeacherType(): String {
        return ConstsDB.TEACHER.lowercase()
    }

    fun getHeadmasterType(): String {
        return ConstsDB.HEADMASTER.lowercase()
    }

    fun getAdminType(): String {
        return ConstsDB.ADMIN.lowercase()
    }

    fun isAllowedType(type: String): Boolean {
        return allowedTypes.contains(type)
    }
}
