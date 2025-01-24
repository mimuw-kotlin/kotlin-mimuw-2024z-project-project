package com.modules

import com.modules.constants.AppConsts
import com.modules.db.dataModels.StudentModel
import com.modules.db.dataModels.TeacherModel
import com.modules.db.other.UserTypes
import com.modules.db.repos.PasswordRepo
import com.modules.db.repos.StudentRepo
import com.modules.db.repos.TeacherRepo
import com.modules.db.reposInterfaces.PasswordInterface
import com.modules.db.reposInterfaces.SchoolUsersInterface
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.ThymeleafContent

fun generateRandomString(length: Int = 8): String {
    val chars = "0123456789"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}

suspend fun checkIfLoggedIn(call: ApplicationCall) {
    val existingSession = call.sessions.get<UserSession>()

    if (existingSession != null) {
        call.respondRedirect("/home")
    }
}

fun Application.configureRouting(
    studentRepo: SchoolUsersInterface<StudentModel>,
    teacherRepo: SchoolUsersInterface<TeacherModel>,
    passwordRepo: PasswordInterface,
) {
    routing {
        get("/") {
            call.response.status(HttpStatusCode.OK)
            call.respond(ThymeleafContent("beforeLogin/startPage", mapOf(AppConsts.SESSION to AppConsts.EMPTY_STRING)))
        }

        get("/loginForm") {
            checkIfLoggedIn(call)
            val queryParams = call.request.queryParameters
            if (queryParams.isEmpty()) {
                call.response.status(HttpStatusCode.OK)
                call.respond(ThymeleafContent("beforeLogin/loginForm", mapOf(AppConsts.SESSION to AppConsts.EMPTY_STRING)))
                return@get
            }
            call.response.status(HttpStatusCode.Unauthorized)
            call.respond(ThymeleafContent("beforeLogin/loginForm", mapOf(AppConsts.SESSION to queryParams[AppConsts.SESSION]!!)))
            return@get
        }

        route("/register") {
            get("/student") {
                checkIfLoggedIn(call)
                call.respond(ThymeleafContent("beforeLogin/registerStudent", emptyMap()))
            }

            post("/student") {
                checkIfLoggedIn(call)

                val post = call.receiveParameters()
                val username = post[AppConsts.USERNAME]
                val password = post[AppConsts.PASSWORD]

                if (username != null && password != null) {
                    if (username.length < AppConsts.MIN_USERNAME_LEN || password.length < AppConsts.MIN_PASSWORD_LEN) {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(ThymeleafContent("beforeLogin/registerStudent", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
                        return@post
                    }
                    if (!studentRepo.addRow(
                            StudentModel(
                                index = generateRandomString(),
                                username = username,
                                userType = UserTypes.getStudentType(),
                                classNbr = AppConsts.N_A,
                                active = false,
                            )
                        )
                        )
                    {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(ThymeleafContent("beforeLogin/registerStudent", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
                        return@post
                    }
                    passwordRepo.setPassword(username, password)
                    call.respond(ThymeleafContent("beforeLogin/registerStudent", mapOf(AppConsts.SESSION to AppConsts.SUCCESS)))
                    return@post
                } else {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(ThymeleafContent("beforeLogin/registerStudent", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
                    return@post
                }
            }

            get("/teacher") {
                checkIfLoggedIn(call)
                call.respond(ThymeleafContent("beforeLogin/registerTeacher", emptyMap()))
            }

            post("/teacher") {
                checkIfLoggedIn(call)

                val post = call.receiveParameters()
                val username = post[AppConsts.USERNAME]
                val password = post[AppConsts.PASSWORD]

                if (username != null && password != null) {
                    if (username.length < AppConsts.MIN_USERNAME_LEN || password.length < AppConsts.MIN_PASSWORD_LEN) {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(ThymeleafContent("beforeLogin/registerTeacher", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
                        return@post
                    }
                    if (!teacherRepo.addRow(
                        TeacherModel(
                            index = generateRandomString(),
                            username = username,
                            userType = UserTypes.getTeacherType(),
                            classNbr = AppConsts.N_A,
                            subjectIndex = AppConsts.N_A,
                            active = false,
                        ),
                    )
                    )
                    {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(ThymeleafContent("beforeLogin/registerTeacher", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
                        return@post
                    }
                    passwordRepo.setPassword(username, password)
                    call.respond(ThymeleafContent("beforeLogin/registerTeacher", mapOf(AppConsts.SESSION to AppConsts.SUCCESS)))
                    return@post
                } else {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(ThymeleafContent("beforeLogin/registerTeacher", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
                    return@post
                }
            }
        }

        authenticate(AppConsts.BASIC_AUTH_SESSION) {
            get("/home") {
                val session = call.sessions.get<UserSession>()
                if (session != null) {
                    if (session.userType == UserTypes.getStudentType()) {
                        call.respondRedirect("/student/home")
                        return@get
                    } else if (session.userType == UserTypes.getAdminType()) {
                        call.respondRedirect("/admin/controlPanel")
                        return@get
                    } else {
                        call.respondRedirect("/teacher/home")
                        return@get
                    }
                }
                call.respondRedirect("/loginForm")
            }
        }
    }
}
