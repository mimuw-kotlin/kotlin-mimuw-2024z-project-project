package com.modules

import com.modules.constants.AppConsts
import com.modules.db.dataModels.StudentModel
import com.modules.db.dataModels.TeacherModel
import com.modules.db.other.UserTypes
import com.modules.db.repos.AdminRepo
import com.modules.db.repos.PasswordRepo
import com.modules.db.repos.StudentRepo
import com.modules.db.repos.TeacherRepo
import com.modules.db.reposInterfaces.AdminInterface
import com.modules.db.reposInterfaces.PasswordInterface
import com.modules.db.reposInterfaces.SchoolUsersInterface
import com.modules.utils.checkIfActive
import com.modules.utils.checkPassword
import com.modules.utils.checkUserType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.ThymeleafContent
import kotlinx.serialization.Serializable

// Username value can be either username or index - currently only username is supported
// TODO(In userSession we should store not username but INDEX)
@Serializable
data class UserSession(val username: String, val userType: String)

fun Application.configureSecurity(
    pswdRepo: PasswordInterface,
    teacherRepo: SchoolUsersInterface<TeacherModel>,
    studentRepo: SchoolUsersInterface<StudentModel>,
    adminRepo: AdminInterface,
) {
    authentication {
        form(name = AppConsts.LOGIN_FORM_AUTH) {
            userParamName = AppConsts.USERNAME
            passwordParamName = AppConsts.PASSWORD

            validate { credentials ->
                if (checkPassword(pswdRepo, credentials.name, credentials.password)) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }

            challenge {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respondRedirect("/loginForm?session=invalidCred")
            }
        }
    }

    routing {
        authenticate(AppConsts.LOGIN_FORM_AUTH) {
            post("/login") {
                val existingSession = call.sessions.get<UserSession>()
                if (existingSession != null) {
                    call.respondRedirect("/home")
                    return@post
                } else {
                    val userName = call.principal<UserIdPrincipal>()?.name.toString()
                    try {
                        val userType = checkUserType(userName, teacherRepo, studentRepo, adminRepo)

                        if (!checkIfActive(userType, userName, studentRepo, teacherRepo)) {
                            call.response.status(HttpStatusCode.Forbidden)
                            call.respondRedirect("/loginForm?" + AppConsts.SESSION + AppConsts.EQUALS + AppConsts.INACTIVE)
                            return@post
                        }

                        call.sessions.set(UserSession(userName, userType))
                        when (userType) {
                            UserTypes.getStudentType() -> {
                                call.response.status(HttpStatusCode.OK)
                                call.respond(
                                    ThymeleafContent("afterLogin/loggedIN", mapOf(AppConsts.USERNAME to userName)),
                                )
                                return@post
                            }
                            UserTypes.getTeacherType() -> {
                                call.response.status(HttpStatusCode.OK)
                                call.respond(
                                    ThymeleafContent("afterLogin/loggedIN", mapOf(AppConsts.USERNAME to userName)),
                                )
                                return@post
                            }

                            UserTypes.getHeadmasterType() -> {
                                call.response.status(HttpStatusCode.OK)
                                call.respond(
                                    ThymeleafContent("afterLogin/loggedIN", mapOf(AppConsts.USERNAME to userName)),
                                )
                                return@post
                            }
                            UserTypes.getAdminType() -> {
                                call.response.status(HttpStatusCode.OK)
                                call.respondRedirect("/admin/controlPanel")
                                return@post
                            }
                        }
                    } catch (e: Exception) {
                        println("\u001B[33m Exception: $e \u001B[0m")
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respondRedirect("/loginForm?" + AppConsts.SESSION + AppConsts.EQUALS + AppConsts.INVALID_CRED)
                    }
                }
            }
        }

        authenticate(AppConsts.BASIC_AUTH_SESSION) {
            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/")
            }
        }
    }
}
