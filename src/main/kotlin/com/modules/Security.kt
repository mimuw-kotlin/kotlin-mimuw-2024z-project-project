package com.modules

import com.modules.constants.AppConsts
import com.modules.db.other.PswdCheckRetVal
import com.modules.db.other.UserTypes
import com.modules.db.repos.AdminRepo
import com.modules.db.repos.PasswordRepo
import com.modules.db.repos.StudentRepo
import com.modules.db.repos.TeacherRepo
import com.modules.utils.checkIfActive
import com.modules.utils.checkPassword
import com.modules.utils.checkUserType
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.ThymeleafContent
import kotlinx.serialization.Serializable

// Username value can be either username or index - currently only username is supported
@Serializable
data class UserSession(val username: String, val userType: String)

fun Application.configureSecurity(pswdRepo: PasswordRepo,
                                  teacherRepo: TeacherRepo,
                                  studentRepo: StudentRepo,
                                  adminRepo: AdminRepo) {
    authentication {
        form(name = "login-form-auth") {
            userParamName = AppConsts.USERNAME
            passwordParamName = AppConsts.PASSWORD

            validate { credentials ->
                if (checkPassword(pswdRepo, credentials.name, credentials.password))
                    UserIdPrincipal(credentials.name)
                else
                    null
            }

            challenge {
                call.respondRedirect("/loginForm?session=invalidCred")
            }
        }
    }

    routing {
        authenticate("login-form-auth") {
            post("/login"){

                val existingSession = call.sessions.get<UserSession>()
                if (existingSession != null)
                    call.respondRedirect("/home")
                else
                {
                    val userName = call.principal<UserIdPrincipal>()?.name.toString()
                    try {
                        val userType = checkUserType(userName, teacherRepo, studentRepo, adminRepo)

                        if (!checkIfActive(userType, userName, studentRepo, teacherRepo))
                            call.respondRedirect("/loginForm?session=inactive")

                        call.sessions.set(UserSession(userName, userType))
                        when (userType) {
                            UserTypes.getStudentType() -> call.respond(ThymeleafContent("afterLogin/loggedIN", mapOf("username" to userName)))
                            UserTypes.getTeacherType() -> call.respond(ThymeleafContent("afterLogin/loggedIN", mapOf("username" to userName)))
                            UserTypes.getAdminType() -> call.respondRedirect("/admin/home")
                        }
                    }
                    catch (e: Exception) {

                        call.respondRedirect("/loginForm?session=invalidCred")
                    }
                }
            }
        }

        authenticate("auth-session") {
            get ("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/")
            }
        }
    }
}