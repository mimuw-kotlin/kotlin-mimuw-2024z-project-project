package com.modules

import com.modules.constants.AppConsts
import com.modules.db.other.UserTypes
import com.modules.db.repos.AdminRepo
import com.modules.db.repos.PasswordRepo
import com.modules.db.repos.StudentRepo
import com.modules.db.repos.TeacherRepo
import com.modules.utils.checkEditUserParams
import com.modules.utils.checkUserType
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*

fun Application.configureRoutingAdmin(studentRepo: StudentRepo,
                                 teacherRepo: TeacherRepo,
                                 passwordRepo: PasswordRepo,
                                 adminRepo: AdminRepo
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
                    call.respond(ThymeleafContent("admin/editUsers",
                        mapOf("students" to students, "teachers" to teachers)))
                }

                post("/editUser") {
                    val post = call.receiveParameters()
                    val userIndex = post["index"]
                    val userName = post["username"]
                    val classNbr = post["classNbr"]
                    val active = post["active"]
                    val userType = post["userType"]

                    if (userIndex != null && userName != null && classNbr != null && active != null && userType != null) {

                        if (!checkEditUserParams(post, studentRepo, teacherRepo))
                            call.respondRedirect("/admin/editUsers?error=1")

                        val realUserType = checkUserType(userIndex, teacherRepo, studentRepo)

                        if (realUserType == UserTypes.getStudentType() && userType != realUserType)
                            call.respondRedirect("/admin/editUsers?error=2")
                        val boolActive = active == "true"
                        when (realUserType) {
                            UserTypes.getStudentType() -> studentRepo.updateRow(userIndex, userName, userType, classNbr, boolActive)
                            UserTypes.getTeacherType() -> teacherRepo.updateRow(userIndex, userName, userType, classNbr, boolActive)
                            UserTypes.getHeadmasterType() -> teacherRepo.updateRow(userIndex, userName, userType, classNbr, boolActive)
                            }
                        }
                    }
                }

                get("/activateUsers") {
                    call.respondText("Activate user")
                }

                get("/showUsers") {
                    call.respondText("Show users")
                }

            }
        }
    }


