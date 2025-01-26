package com.modules.utils

import com.modules.constants.AppConsts
import com.modules.db.dataModels.StudentModel
import com.modules.db.dataModels.TeacherModel
import com.modules.db.other.PswdCheckRetVal
import com.modules.db.other.UserTypes
import com.modules.db.repos.*
import com.modules.db.reposInterfaces.AdminInterface
import com.modules.db.reposInterfaces.PasswordInterface
import com.modules.db.reposInterfaces.SchoolUsersInterface
import io.ktor.http.Parameters

suspend fun checkPassword(
    pswdRepo: PasswordInterface,
    username: String,
    password: String,
): Boolean {
    val pswdCheckRetVal = pswdRepo.checkPassword(username, password)
    return when (pswdCheckRetVal) {
        PswdCheckRetVal.USER_NOT_FOUND -> false
        PswdCheckRetVal.PASSWORD_INCORRECT -> false
        PswdCheckRetVal.PASSWORD_CORRECT -> true
    }
}

suspend fun checkUserType(
    username: String,
    teacherRepo: SchoolUsersInterface<TeacherModel>,
    studentRepo: SchoolUsersInterface<StudentModel>,
    adminRepo: AdminInterface,
): String {
    // We make three queries to the db, which is not optimal
    // we should make a join query instead, but current impl of repos
    // does not support it
    val student = studentRepo.getByUsername(username)
    if (student != null) {
        return UserTypes.getStudentType()
    }

    val teacher = teacherRepo.getByUsername(username)
    if (teacher != null) {
        return UserTypes.getType(teacher.userType)
    }

    val admin = adminRepo.getByUsername(username)
    if (admin != null) {
        return UserTypes.getAdminType()
    }

    throw Exception("User not found")
}

suspend fun checkUserType(
    index: String,
    teacherRepo: SchoolUsersInterface<TeacherModel>,
    studentRepo: SchoolUsersInterface<StudentModel>,
): String {
    // We make three queries to the db, which is not optimal
    // we should make a join query instead, but current impl of repos
    // does not support it
    val student = studentRepo.getByIndex(index)
    if (student != null) {
        return UserTypes.getStudentType()
    }

    val teacher = teacherRepo.getByIndex(index)
    if (teacher != null) {
        return UserTypes.getType(teacher.userType)
    }

    throw Exception("User not found")
}

suspend fun checkIfActive(
    userType: String,
    username: String,
    studentRepo: SchoolUsersInterface<StudentModel>,
    teacherRepo: SchoolUsersInterface<TeacherModel>,
): Boolean {
    if (userType == UserTypes.getStudentType()) {
        val student = studentRepo.getByUsername(username)
        if (student != null) {
            return student.active
        }
    } else if (userType == UserTypes.getTeacherType()) {
        val teacher = teacherRepo.getByUsername(username)
        if (teacher != null) {
            return teacher.active
        }
    } else if (userType == UserTypes.getHeadmasterType()) {
        val teacher = teacherRepo.getByUsername(username)
        if (teacher != null) {
            return teacher.active
        }
    } else if (userType == UserTypes.getAdminType()) {
        return true
    }
    return false
}

suspend fun checkEditUserParams(
    post: Parameters,
    studentRepo: SchoolUsersInterface<StudentModel>,
    teacherRepo: SchoolUsersInterface<TeacherModel>,
    classRepo: ClassRepo,
): Boolean {
    val userIndex = post[AppConsts.INDEX]
    val userName = post[AppConsts.USERNAME]
    val classNbr = post[AppConsts.CLASS_NBR]
    val active = post[AppConsts.ACTIVE]
    val userType = post[AppConsts.USER_TYPE]

    val red = "\u001B[31m"
    val reset = "\u001B[0m"
    val yellow = "\u001B[33m"

    println(red + "userIndex= $userIndex" + reset)
    println(red + "userName= $userName" + reset)
    println(red + "classNbr= $classNbr" + reset)
    println(red + "active= $active" + reset)
    println(red + "userType= $userType" + reset)

    if (userIndex == null || userName == null || classNbr == null || active == null || userType == null) {
        return false
    }

    if (!checkUsername(userName)) {
        return false
    }

    if (classRepo.getByClassNbr(classNbr) == null) {
        return false
    }

    if (active.lowercase() != "true" && active.lowercase() != "false") {
        return false
    }

    if (!UserTypes.isAllowedType(userType)) {
        return false
    }

    return !(studentRepo.getByIndex(userIndex) == null && teacherRepo.getByIndex(userIndex) == null)
}

fun checkUsername(username: String): Boolean {
    val nameRegex = Regex(AppConsts.USERNAME_REGEX)
    return nameRegex.matches(username)
}

suspend fun checkSubjectIndex(
    subjectIndex: String,
    subjectRepo: SubjectRepo,
): Boolean {
    return subjectRepo.getByIndex(subjectIndex) != null
}

fun checkAddSubjectParams(post: Parameters): Boolean {
    val subjectIndex = post[AppConsts.INDEX]
    val subjectName = post[AppConsts.NAME]
    val description = post[AppConsts.DESCRIPTION]

    if (subjectIndex == null || subjectName == null || description == null) {
        return false
    }

    val indexRegex = Regex(AppConsts.SUBJECT_INDEX_REGEX)
    if (!indexRegex.matches(subjectIndex) || subjectIndex.length <= 1 || subjectIndex.length > 20) {
        return false
    }

    if (subjectName.length <= 1 || subjectName.length > 70) {
        return false
    }

    if (description.length > 555) {
        return false
    }

    return true
}
