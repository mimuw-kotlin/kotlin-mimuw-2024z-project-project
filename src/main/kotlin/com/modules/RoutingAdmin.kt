package com.modules

import com.modules.constants.AppConsts
import com.modules.db.dataModels.SubjectModel
import com.modules.db.other.UserTypes
import com.modules.db.repos.*
import com.modules.utils.checkAddSubjectParams
import com.modules.utils.checkEditUserParams
import com.modules.utils.checkSubjectIndex
import com.modules.utils.checkUserType
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*

fun Application.configureRoutingAdmin(
    studentRepo: StudentRepo,
    teacherRepo: TeacherRepo,
    passwordRepo: PasswordRepo,
    adminRepo: AdminRepo,
    classRepo: ClassRepo,
    subjectRepo: SubjectRepo,
) {
    routing {
        authenticate(AppConsts.ADMIN_SESSION) {
            route("/admin") {
                get("/controlPanel") {
                    call.respond(ThymeleafContent("admin/controlPanel", emptyMap()))
                }

                get("/deleteUsers") {
                    val students = studentRepo.getAll()
                    val teachers = teacherRepo.getAll()
                    call.respond(
                        ThymeleafContent(
                            "admin/deleteUsers",
                            mapOf(AppConsts.STUDENTS to students, AppConsts.TEACHERS to teachers),
                        ),
                    )
                }

                post("/deleteUser") {
                    val post = call.receiveParameters()
                    val userIndex = post[AppConsts.INDEX]
                    if (userIndex != null) {
                        val userType = checkUserType(userIndex, teacherRepo, studentRepo)
                        when (userType) {
                            UserTypes.getStudentType() -> studentRepo.removeByIndex(userIndex)
                            UserTypes.getTeacherType() -> teacherRepo.removeByIndex(userIndex)
                        }
                    }
                    call.respondRedirect("/admin/deleteUsers")
                }

                get("/editUsers") {
                    val students = studentRepo.getAll()
                    val teachers = teacherRepo.getAll()
                    val queryParams = call.request.queryParameters

                    if (queryParams.isEmpty()) {
                        call.respond(
                            ThymeleafContent(
                                "admin/editUsers",
                                mapOf(AppConsts.STUDENTS to students, AppConsts.TEACHERS to teachers),
                            ),
                        )
                        return@get
                    }

                    call.respond(
                        ThymeleafContent(
                            "admin/editUsers",
                            mapOf(
                                AppConsts.STUDENTS to students,
                                AppConsts.TEACHERS to teachers,
                                AppConsts.STATUS to queryParams[AppConsts.STATUS]!!,
                            ),
                        ),
                    )
                }

                get("/editChosenUser") {
                    val queryParams = call.request.queryParameters
                    val userIndex = queryParams[AppConsts.INDEX]
                    if (userIndex != null) {
                        val userType = checkUserType(userIndex, teacherRepo, studentRepo)
                        val allClasses = classRepo.getAll()
//                      STUDENT
                        if (userType == UserTypes.getStudentType()) {
                            val user = studentRepo.getByIndex(userIndex)
                            if (user != null) {
                                call.respond(
                                    ThymeleafContent(
                                        "admin/editChosenUser",
                                        mapOf(
                                            AppConsts.USER to user,
                                            AppConsts.CLASSES to allClasses,
                                        ),
                                    ),
                                )
                                return@get
                            }
                            call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "noStudentWithGivenIndex")
                            return@get
                        }
//                      TEACHER OR HEADMASTER
                        else {
                            val user = teacherRepo.getByIndex(userIndex)
                            if (user != null) {
                                val allSubjects = subjectRepo.getAll()
                                call.respond(
                                    ThymeleafContent(
                                        "admin/editChosenUser",
                                        mapOf(
                                            AppConsts.USER to user,
                                            AppConsts.SUBJECTS to allSubjects,
                                            AppConsts.CLASSES to allClasses,
                                        ),
                                    ),
                                )
                                return@get
                            }
                            call.respondRedirect("/admin/editUsers? " + AppConsts.STATUS + AppConsts.EQUALS + "NoTeacherWithGivenIndex")
                            return@get
                        }
                    }
                    call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "givenIndexIsNull")
                }

                post("/editUser") {
                    val post = call.receiveParameters()
                    val userType = post[AppConsts.USER_TYPE]
                    val userIndex = post[AppConsts.INDEX]
                    val username = post[AppConsts.USERNAME]
                    val classNbr = post[AppConsts.CLASS_NBR]
                    val active = post[AppConsts.ACTIVE]

                    if (userType == null) {
                        call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "userTypeParamIsNull")
                        return@post
                    }
                    if (!UserTypes.isAllowedType(userType)) {
                        call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "userTypeNotAllowed")
                        return@post
                    }
                    if (userIndex == null || username == null || classNbr == null || active == null) {
                        call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "someParamIsNull")
                        return@post
                    }

                    if (!checkEditUserParams(post, studentRepo, teacherRepo, classRepo)) {
                        call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "oneOrMoreParamsInvalid")
                        return@post
                    }

                    val realUserType = checkUserType(userIndex, teacherRepo, studentRepo)
                    if (realUserType == UserTypes.getStudentType() && userType != realUserType) {
                        call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "studentTypeMismatch")
                        return@post
                    }

                    val oldUsername =
                        when (realUserType) {
                            UserTypes.getStudentType() -> studentRepo.getByIndex(userIndex)?.username
                            UserTypes.getTeacherType() -> teacherRepo.getByIndex(userIndex)?.username
                            UserTypes.getHeadmasterType() -> teacherRepo.getByIndex(userIndex)?.username
                            else -> null
                        }

//                      this means that somebody is trying to change index of a user
                    if (oldUsername == null) {
                        call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "noUserWithGivenIndex")
                        return@post
                    }

                    val boolActive: Boolean = active.lowercase() == "true"

                    if (userType == UserTypes.getStudentType()) {
                        if (oldUsername != username) {
                            passwordRepo.updateUsername(oldUsername, username)
                        }

                        studentRepo.updateRow(
                            userIndex,
                            username,
                            userType,
                            classNbr,
                            boolActive,
                        )
                    }
//                  teacher or headmaster
                    else {
                        val subjectIndex = post[AppConsts.SUBJECT_INDEX]
                        if (subjectIndex == null) {
                            call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "subjectIndexIsNull")
                            return@post
                        }
                        if (!checkSubjectIndex(subjectIndex, subjectRepo)) {
                            call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "subjectIndexNotFound")
                            return@post
                        }

                        if (classRepo.getTeacherFromClass(classNbr) != null) {
                            call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "classAlreadyHasTeacher")
                            return@post
                        }

                        if (oldUsername != username) {
                            passwordRepo.updateUsername(oldUsername, username)
                        }
                        teacherRepo.updateRow(
                            userIndex,
                            username,
                            userType,
                            classNbr,
                            subjectIndex,
                            boolActive,
                        )
                        classRepo.updateRow(classNbr, username)
                    }
                    call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "0")
                    return@post
                }

                get("/activateUsers") {
                    val students = studentRepo.getAll()
                    val teachers = teacherRepo.getAll()
                    val params = call.request.queryParameters
                    if (params.isEmpty()) {
                        call.respond(
                            ThymeleafContent(
                                "admin/activateUsers",
                                mapOf(AppConsts.STUDENTS to students, AppConsts.TEACHERS to teachers),
                            ),
                        )
                        return@get
                    }
                    call.respond(
                        ThymeleafContent(
                            "admin/activateUsers",
                            mapOf(
                                AppConsts.STUDENTS to students,
                                AppConsts.TEACHERS to teachers,
                                AppConsts.STATUS to params[AppConsts.STATUS]!!,
                            ),
                        ),
                    )
                }

                post("/activateUser") {
                    val post = call.receiveParameters()
                    val userIndex = post[AppConsts.INDEX]

                    if (userIndex == null) {
                        call.respondRedirect("/admin/activateUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "indexIsNull")
                        return@post
                    }

                    try {
                        val userType = checkUserType(userIndex, teacherRepo, studentRepo)
                        val success =
                            when (userType) {
                                UserTypes.getStudentType() -> studentRepo.toggleActiveByIndex(userIndex)
                                UserTypes.getTeacherType() -> teacherRepo.toggleActiveByIndex(userIndex)
                                UserTypes.getHeadmasterType() -> teacherRepo.toggleActiveByIndex(userIndex)
                                else -> false
                            }
                        if (success) {
                            call.respondRedirect("/admin/activateUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "success")
                            return@post
                        }
                        call.respondRedirect("/admin/activateUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "changingActiveFailed")
                        return@post
                    } catch (e: Exception) {
                        call.respondRedirect("/admin/activateUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "userNotFound")
                        return@post
                    }
                }

                get("/subjects") {
                    val subjects = subjectRepo.getAll()
                    val params = call.request.queryParameters
                    if (params.isEmpty()) {
                        call.respond(
                            ThymeleafContent(
                                "admin/subjects",
                                mapOf(AppConsts.SUBJECTS to subjects),
                            ),
                        )
                        return@get
                    }
                    call.respond(
                        ThymeleafContent(
                            "admin/subjects",
                            mapOf(
                                AppConsts.SUBJECTS to subjects,
                                AppConsts.STATUS to params[AppConsts.STATUS]!!,
                            ),
                        ),
                    )
                }

                post("/addSubject") {
                    val post = call.receiveParameters()
                    val subjectIndex = post[AppConsts.INDEX]
                    val subjectName = post[AppConsts.NAME]
                    val description = post[AppConsts.DESCRIPTION]

                    if (subjectIndex == null || subjectName == null || description == null) {
                        call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "oneOrMoreParamsAreNull")
                        return@post
                    }

                    if (!checkAddSubjectParams(post)) {
                        call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "oneOrMoreParamsInvalid")
                        return@post
                    }

                    if (subjectRepo.getByIndex(subjectIndex) != null) {
                        call.respondRedirect(
                            "/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "subjectWithGivenIndexAlreadyExists",
                        )
                        return@post
                    }
                    subjectRepo.addRow(SubjectModel(subjectIndex, subjectName, description))
                    call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "success")
                    return@post
                }

                get("/editSubject") {
                    val queryParams = call.request.queryParameters
                    val subjectIndex = queryParams[AppConsts.INDEX]
                    if (subjectIndex != null) {
                        val subject = subjectRepo.getByIndex(subjectIndex)
                        if (subject != null) {
                            call.respond(
                                ThymeleafContent(
                                    "admin/editSubject",
                                    mapOf(AppConsts.SUBJECT to subject),
                                ),
                            )
                            return@get
                        }
                        call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "noSubjectWithGivenIndex")
                        return@get
                    }
                    call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "givenSubjectIndexIsNull")
                }

                post("/editSubject") {
                    val post = call.receiveParameters()
                    val subjectIndex = post[AppConsts.INDEX]
                    val subjectName = post[AppConsts.NAME]
                    val description = post[AppConsts.DESCRIPTION]

                    if (subjectIndex == null || subjectName == null || description == null) {
                        call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "oneOrMoreParamsAreNullInEdit")
                        return@post
                    }

                    if (!checkAddSubjectParams(post)) {
                        call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "oneOrMoreParamsInvalidInEdit")
                        return@post
                    }

                    if (subjectRepo.getByIndex(subjectIndex) == null) {
                        call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "noSubjectWithGivenIndexInEdit")
                        return@post
                    }

                    subjectRepo.updateRow(subjectIndex, subjectName, description)
                    call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "success")
                    return@post
                }

                post("/deleteSubject") {
                    val post = call.receiveParameters()
                    val subjectIndex = post[AppConsts.INDEX]
                    if (subjectIndex != null) {
                        if (subjectRepo.removeByIndex(subjectIndex))
                            {
                                call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "success")
                                return@post
                            }
                        call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "subjectNotFoundInDelete")
                        return@post
                    }
                    call.respondRedirect("/admin/subjects?" + AppConsts.STATUS + AppConsts.EQUALS + "subjectIndexIsNullInDelete")
                    return@post
                }
            }
        }
    }
}
