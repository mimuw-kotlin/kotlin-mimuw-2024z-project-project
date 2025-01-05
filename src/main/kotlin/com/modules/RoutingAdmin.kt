package com.modules

import com.modules.constants.AppConsts
import com.modules.db.other.UserTypes
import com.modules.db.repos.*
import com.modules.utils.checkEditUserParams
import com.modules.utils.checkUserType
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import kotlin.reflect.typeOf

fun Application.configureRoutingAdmin(studentRepo: StudentRepo,
                                    teacherRepo: TeacherRepo,
                                    passwordRepo: PasswordRepo,
                                    adminRepo: AdminRepo,
                                    classRepo: ClassRepo
) {
    routing {
        authenticate("admin-session") {
            route("/admin") {
                get("/controlPanel") {
                    call.respond(ThymeleafContent("admin/controlPanel", emptyMap()))
                }

                get("/deleteUsers") {
                    val students = studentRepo.getAll()
                    val teachers = teacherRepo.getAll()
                    call.respond(ThymeleafContent("admin/deleteUsers",
                            mapOf(AppConsts.STUDENTS to students, AppConsts.TEACHERS to teachers)))
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

                    if (queryParams.isEmpty())
                    {
                        call.respond(ThymeleafContent("admin/editUsers",
                            mapOf(AppConsts.STUDENTS to students, AppConsts.TEACHERS to teachers)))
                        return@get
                    }

                    call.respond(ThymeleafContent("admin/editUsers",
                        mapOf(AppConsts.STUDENTS to students, AppConsts.TEACHERS to teachers, AppConsts.STATUS to queryParams[AppConsts.STATUS]!!)))
                }

                get("/editChosenUser") {
                    val queryParams = call.request.queryParameters
                    val userIndex = queryParams[AppConsts.INDEX]
                    if (userIndex != null) {
                        val userType = checkUserType(userIndex, teacherRepo, studentRepo)
                        if (userType == UserTypes.getStudentType())
                        {
                            val user = studentRepo.getByIndex(userIndex)
                            if (user != null) {
                                call.respond(ThymeleafContent("admin/editChosenUser",
                                    mapOf(AppConsts.USER to user)))
                                return@get
                            }
                            call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "noStudentWithGivenIndex")
                            return@get
                        }
                        else
                        {
                            val user = teacherRepo.getByIndex(userIndex)
                            if (user != null) {
                                call.respond(ThymeleafContent("admin/editChosenUser",
                                    mapOf(AppConsts.USER to user)))
                                return@get
                            }
                            call.respondRedirect("/admin/editUsers? " + AppConsts.STATUS + AppConsts.EQUALS +"NoTeacherWithGivenIndex")
                            return@get
                        }
                    }
                    call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "givenIndexIsNull")
                }

                post("/editUser") {
                    val post = call.receiveParameters()
                    val userIndex = post[AppConsts.INDEX]
                    val username = post[AppConsts.USERNAME]
                    val classNbr = post[AppConsts.CLASS_NBR]
                    val active = post[AppConsts.ACTIVE]
                    val userType = post[AppConsts.USER_TYPE]

                    if (userIndex != null && username != null && classNbr != null && active != null && userType != null) {

                        if (!checkEditUserParams(post, studentRepo, teacherRepo, classRepo)) {
                            call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS +"oneOrMoreParamsInvalid")
                            return@post
                        }

                        val realUserType = checkUserType(userIndex, teacherRepo, studentRepo)
                        if (realUserType == UserTypes.getStudentType() && userType != realUserType) {
                            call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "studentTypeMismatch")
                            return@post
                        }

                        val oldUsername = when (realUserType) {
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

                        if (oldUsername != username)
                            passwordRepo.updateUsername(oldUsername, username)

                        val boolActive: Boolean = active.lowercase() == "true"

                        when (realUserType) {
                            UserTypes.getStudentType() -> studentRepo.updateRow(userIndex, username, userType, classNbr, boolActive)
                            UserTypes.getTeacherType() -> teacherRepo.updateRow(userIndex, username, userType, classNbr, boolActive)
                            UserTypes.getHeadmasterType() -> teacherRepo.updateRow(userIndex, username, userType, classNbr, boolActive)
                        }
                        call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "0")
                        return@post
                    }
                    call.respondRedirect("/admin/editUsers?" + AppConsts.STATUS + AppConsts.EQUALS + "someParamIsNull")
                }

                }


                }
        authenticate("auth-session") {

            get("/activateUsers") {
                call.respondText("Activate user")
            }
        }
            }
        }


