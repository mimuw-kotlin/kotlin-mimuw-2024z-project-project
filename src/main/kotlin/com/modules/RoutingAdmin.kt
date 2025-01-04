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
                    val yellow = "\u001B[33m"
                    val green = "\u001B[32m"
                    val reset = "\u001B[0m"

                    for (teacher in teachers) {
                        println(green + "userIndex: ${teacher.index}" + reset)
                        println(green + "username: ${teacher.username}" + reset)
                        println(green + "classNbr: ${teacher.classNbr}" + reset)
                        println(green + "active: ${teacher.active}" + reset)
                        println(green + "userType: ${teacher.userType}" + reset)
                    }

                    val queryParams = call.request.queryParameters
                    if (queryParams.isEmpty())
                        call.respond(ThymeleafContent("admin/editUsers",
                            mapOf("students" to students, "teachers" to teachers)))

                    println("#######------STATUS: ${queryParams["status"]}")
                    call.respond(ThymeleafContent("admin/editUsers",
                        mapOf("students" to students, "teachers" to teachers, "status" to queryParams["status"]!!)))
                }

                get("/editChosenUser") {
                    val queryParams = call.request.queryParameters
                    val userIndex = queryParams["index"]
                    if (userIndex != null) {
                        val userType = checkUserType(userIndex, teacherRepo, studentRepo)
                        if (userType == UserTypes.getStudentType())
                        {
                            val user = studentRepo.getByIndex(userIndex)
                            if (user != null) {
                                call.respond(ThymeleafContent("admin/editChosenUser",
                                    mapOf("user" to user)))
                            }
                            call.respondRedirect("/admin/editUsers?status=studentEditChosen")
                        }
                        else
                        {
                            val user = teacherRepo.getByIndex(userIndex)
                            if (user != null) {
                                call.respond(ThymeleafContent("admin/editChosenUser",
                                    mapOf("user" to user)))
                            }
                            call.respondRedirect("/admin/editUsers?status=TeacherEditChosen")
                        }
                    }
                    call.respondRedirect("/admin/editUsers?status=userIndexNull")
                }

                post("/editUser") {
                    val post = call.receiveParameters()
                    val userIndex = post["index"]
                    val username = post["username"]
                    val classNbr = post["classNbr"]
                    val active = post["active"]
                    val userType = post["userType"]

                    val yellow = "\u001B[33m"
                    val green = "\u001B[32m"
                    val reset = "\u001B[0m"

                    println(green + "userIndex: $userIndex" + reset)
                    println(green + "username: $username" + reset)
                    println(green + "classNbr: $classNbr" + reset)
                    println(green + "active: $active" + reset)
                    println(green + "userType: $userType" + reset)

                    if (userIndex != null && username != null && classNbr != null && active != null && userType != null) {

                        println(yellow + "check params" + reset)
                        if (!checkEditUserParams(post, studentRepo, teacherRepo, classRepo))
                        {
                            println(yellow + "!!!!!!CHECK EDIT USER PARAMS FAILED!!!!!" + reset)
                            call.respondRedirect("/admin/editUsers?status=editUserParamsError")
                            return@post
                        }

                        println(yellow + "check usertype" + reset)
                        val realUserType = checkUserType(userIndex, teacherRepo, studentRepo)


                        if (realUserType == UserTypes.getStudentType() && userType != realUserType)
                        {
                            call.respondRedirect("/admin/editUsers?status=studentTypeMismatch")
                            return@post
                        }

                        println(yellow + "old username" + reset)
                        val oldUsername = when (realUserType) {
                            UserTypes.getStudentType() -> studentRepo.getByIndex(userIndex)?.username
                            UserTypes.getTeacherType() -> teacherRepo.getByIndex(userIndex)?.username
                            UserTypes.getHeadmasterType() -> teacherRepo.getByIndex(userIndex)?.username
                            else -> null
                        }

//                      this means that somebody is trying to change index of a user
                        if (oldUsername == null)
                        {
                            call.respondRedirect("/admin/editUsers?status=oldUsernameNull")
                            return@post
                        }

                        println(yellow + "old username != username" + reset)
                        if (oldUsername != username)
                            passwordRepo.updateUsername(oldUsername!!, username)

                        val boolActive: Boolean = active == "true"

                        println(yellow + "when" + reset)
                        when (realUserType) {
                            UserTypes.getStudentType() -> studentRepo.updateRow(userIndex, username, userType, classNbr, boolActive)
                            UserTypes.getTeacherType() -> teacherRepo.updateRow(userIndex, username, userType, classNbr, boolActive)
                            UserTypes.getHeadmasterType() -> teacherRepo.updateRow(userIndex, username, userType, classNbr, boolActive)
                            }
                        println(yellow + "AFTER WHEN" + reset)
                        call.respondRedirect("/admin/editUsers?status=0")
                        return@post
                    }
                    call.respondRedirect("/admin/editUsers?status=someParamisNull")
                }

                get("/activateUsers") {
                    call.respondText("Activate user")
                }

                }
            }
        }
    }


