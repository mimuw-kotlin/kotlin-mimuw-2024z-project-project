package com.modules

import com.modules.db.dataModels.StudentModel
import com.modules.db.dataModels.TeacherModel
import com.modules.db.other.ConstsDB
import com.modules.db.other.UserTypes
import com.modules.db.repos.AdminRepo
import com.modules.db.repos.PasswordRepo
import com.modules.db.repos.StudentRepo
import com.modules.db.repos.TeacherRepo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.ThymeleafContent
import com.modules.constants.AppConsts


fun generateRandomString(length: Int = 8): String {
    val chars = "0123456789"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}

suspend fun checkIfLoggedIn(call: ApplicationCall): Unit {
    val existingSession = call.sessions.get<UserSession>()

    if (existingSession != null)
        call.respondRedirect("/home")
}

fun Application.configureRouting(studentRepo: StudentRepo,
                                 teacherRepo: TeacherRepo,
                                 passwordRepo: PasswordRepo,
                                 adminRepo: AdminRepo) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {

        get("/") {
            call.respond(ThymeleafContent("beforeLogin/startPage", mapOf(AppConsts.SESSION to AppConsts.EMPTY_STRING)))
        }

        get("/loginForm") {
            checkIfLoggedIn(call)
            val queryParams = call.request.queryParameters
            if (queryParams.isEmpty())
                call.respond(ThymeleafContent("beforeLogin/loginForm", mapOf(AppConsts.SESSION to AppConsts.EMPTY_STRING)))
            call.respond(ThymeleafContent("beforeLogin/loginForm", mapOf(AppConsts.SESSION to queryParams[AppConsts.SESSION]!!)))

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

                if (username != null && password != null)
                {
                    studentRepo.addRow(StudentModel(
                                        index = generateRandomString(),
                                        username=username,
                                        userType = UserTypes.getType(ConstsDB.STUDENT),
                                        classNbr = AppConsts.N_A,
                                        active = false
                                    )
                    )
                    passwordRepo.setPassword(username, password)
                    call.respond(ThymeleafContent("beforeLogin/registerStudent", mapOf(AppConsts.SESSION to AppConsts.SUCCESS)))
                }
                else
                {

                    call.respond(ThymeleafContent("beforeLogin/registerStudent", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
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

                if (username != null && password != null)
                {
                    teacherRepo.addRow(
                        TeacherModel(
                        index = generateRandomString(),
                        username = username,
                        userType = UserTypes.getType(ConstsDB.TEACHER),
                        classNbr = AppConsts.N_A,
                            subjectIndex = AppConsts.N_A,
                        active = false
                    )
                    )
                    passwordRepo.setPassword(username, password)
                    call.respond(ThymeleafContent("beforeLogin/registerTeacher", mapOf(AppConsts.SESSION to AppConsts.SUCCESS)))
                }
                else
                {
                    call.respond(ThymeleafContent("beforeLogin/registerTeacher", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
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
                    }
                    else
                    {
                        call.respondRedirect("/teacher/home")
                        return@get
                    }
                }
                call.respondRedirect("/loginForm")
            }
        }
    }
}
