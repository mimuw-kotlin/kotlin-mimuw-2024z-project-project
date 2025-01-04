package com.modules

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
                            mapOf("students" to students, "teachers" to teachers)))
                }

                post("/deleteUser") {
                    val post = call.receiveParameters()
                    val userIndex = post["index"]
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
                        call.respond(ThymeleafContent("admin/editUsers",
                            mapOf("students" to students, "teachers" to teachers)))

                    call.respond(ThymeleafContent("admin/editUsers",
                        mapOf("students" to students, "teachers" to teachers, "status" to queryParams["status"]!!)))
                }

                post("/editUser") {
                    val post = call.receiveParameters()
                    val userIndex = post["index"]
                    val username = post["username"]
                    val classNbr = post["classNbr"]
                    val active = post["active"]
                    val userType = post["userType"]

                    if (userIndex != null && username != null && classNbr != null && active != null && userType != null) {

                        if (!checkEditUserParams(post, studentRepo, teacherRepo, classRepo))
                            call.respondRedirect("/admin/editUsers?status=1")

                        val realUserType = checkUserType(userIndex, teacherRepo, studentRepo)

                        if (realUserType == UserTypes.getStudentType() && userType != realUserType)
                            call.respondRedirect("/admin/editUsers?status=2")

                        val oldUsername = when (realUserType) {
                            UserTypes.getStudentType() -> studentRepo.getByIndex(userIndex)?.username
                            UserTypes.getTeacherType() -> teacherRepo.getByIndex(userIndex)?.username
                            UserTypes.getHeadmasterType() -> teacherRepo.getByIndex(userIndex)?.username
                            else -> null
                        }

//                      this means that somebody is trying to change index of a user
                        if (oldUsername == null)
                            call.respondRedirect("/admin/editUsers?status=1")

                        if (oldUsername != username)
                            passwordRepo.updateUsername(oldUsername!!, username)

                        val boolActive: Boolean = active == "true"
                        println("active: $active")
                        println("boolActive: $boolActive")

                        when (realUserType) {
                            UserTypes.getStudentType() -> studentRepo.updateRow(userIndex, username, userType, classNbr, boolActive)
                            UserTypes.getTeacherType() -> teacherRepo.updateRow(userIndex, username, userType, classNbr, boolActive)
                            UserTypes.getHeadmasterType() -> teacherRepo.updateRow(userIndex, username, userType, classNbr, boolActive)
                            }
                        }
                    call.respondRedirect("/admin/editUsers?status=0")
                    }

                get("/activateUsers") {
                    call.respondText("Activate user")
                }

                }
            }
        }
    }


