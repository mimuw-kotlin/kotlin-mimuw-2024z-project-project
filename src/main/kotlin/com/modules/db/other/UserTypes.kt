package com.modules.db.other

// Only admin can add new user types
// Ultimately there will be a sql table for user types
object UserTypes {
    private val allowedTypes = mutableSetOf(
                                ConstsDB.STUDENT,
                                ConstsDB.TEACHER,
                                ConstsDB.HEADMASTER,
                                ConstsDB.ADMIN)

    fun getAllTypes(): Set<String> {
        val mutableList = mutableListOf<String>()
        for (type in allowedTypes) {
            mutableList.add(type)
        }
        return mutableList.toSet()
    }

    fun getType(type: String): String {
        if (type.lowercase() in allowedTypes) {
            return type.lowercase()
        }
        throw Exception("User type not allowed")
    }

    fun getStudentType(): String {
        return ConstsDB.STUDENT
    }

    fun getTeacherType(): String {
        return ConstsDB.TEACHER
    }

    fun getHeadmasterType(): String {
        return ConstsDB.HEADMASTER
    }

    fun getAdminType(): String {
        return ConstsDB.ADMIN
    }



    fun isAllowedType(type: String): Boolean {
        return allowedTypes.contains(type)
    }

    fun addUserType(type: String) {
        allowedTypes.add(type.lowercase())
    }
}