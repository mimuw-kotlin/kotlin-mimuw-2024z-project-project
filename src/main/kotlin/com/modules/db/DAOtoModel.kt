package com.modules.db

import com.modules.db.DAO.*
import com.modules.db.dataModels.*

fun adminDAOToModel(dao: AdminDAO) = AdminModel(
    username = dao.username,
    userType = dao.userType
)

fun studentDAOToModel(dao: StudentsDAO) = StudentModel(
    index = dao.index,
    username = dao.username,
    userType = dao.userType,
    classNbr = dao.classNbr,
    active = dao.active
)

fun teacherDAOToModel(dao: TeachersDAO) = TeacherModel(
    index = dao.index,
    username = dao.username,
    userType = dao.userType,
    classNbr = dao.classNbr,
    subjectIndex = dao.subjectIndex,
    active = dao.active
)

fun classDAOToModel(dao: ClassesDAO) = ClassModel(
    classNbr = dao.classNbr,
    classTeacherName = dao.classTeacherName
)

fun subjectDAOToModel(dao: SubjectsDAO) = SubjectModel(
    index = dao.index,
    name = dao.name,
    description = dao.description
)

fun passwordDAOToModel(dao: PasswordsDAO) = PasswordModel(
    username = dao.username,
    password = dao.password
)