package com.modules

import com.modules.db.other.ConstsDB
import com.modules.db.other.PswdCheckRetVal
import com.modules.db.other.UserTypes
import com.modules.db.repos.AdminRepo
import com.modules.db.repos.PasswordRepo
import com.modules.db.repos.StudentRepo
import com.modules.db.repos.TeacherRepo
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

suspend fun checkPassword(pswdRepo: PasswordRepo, username: String, password: String): Boolean {
    val pswdCheckRetVal = pswdRepo.checkPassword(username, password)
    return when (pswdCheckRetVal) {
        PswdCheckRetVal.USER_NOT_FOUND -> false
        PswdCheckRetVal.PASSWORD_INCORRECT -> false
        PswdCheckRetVal.PASSWORD_CORRECT -> true
    }
}

suspend fun checkUserType(
    username: String,
    teacherRepo: TeacherRepo,
    studentRepo: StudentRepo,
    adminRepo: AdminRepo ): String {
    // We make three queries to the db, which is not optimal
    // we should make a join query instead, but current impl of repos
    // does not support it
    val student = studentRepo.getByUsername(username)
    if (student != null)
        return UserTypes.getType(student.userType)

    val teacher = teacherRepo.getByUsername(username)
    if (teacher != null)
        return UserTypes.getType(teacher.userType)

    val admin = adminRepo.getByUsername(username)
    if (admin != null)
        return UserTypes.getType(admin.userType)

//    TODO("Throw exception")
    return ConstsDB.N_A
}

suspend fun checkIfActive(userType: String, username: String, studentRepo: StudentRepo,
                          teacherRepo: TeacherRepo) : Boolean {
    if (userType == UserTypes.getType(ConstsDB.STUDENT))
    {
        val student = studentRepo.getByUsername(username)
        if (student != null)
            return student.active
    }
    else if (userType == UserTypes.getType(ConstsDB.TEACHER))
    {
        val teacher = teacherRepo.getByUsername(username)
        if (teacher != null)
            return teacher.active
    }
    return false
}

fun Application.configureSecurity(pswdRepo: PasswordRepo,
                                  teacherRepo: TeacherRepo,
                                  studentRepo: StudentRepo,
                                  adminRepo: AdminRepo) {
    authentication {
        form(name = "login-form-auth") {
            userParamName = "username"
            passwordParamName = "password"

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
                    val userType = checkUserType(userName, teacherRepo, studentRepo, adminRepo)

                    if (!checkIfActive(userType, userName, studentRepo, teacherRepo))
                        call.respondRedirect("/loginForm?session=inactive")

                    call.sessions.set(UserSession(userName, userType))
                    call.respond(ThymeleafContent("afterLogin/loggedIN", mapOf("username" to userName)))
                }
            }
        }

        authenticate("auth-session") {
            get("/protected/session") {

                // we use UserPrincipalId only when user is logging in, now we have
                // session instead and UserPrincipalID will be null if we use it in call.principal
                val userSession = call.principal<UserSession>()
//                val userSession = call.sessions.get<UserSession>()
//                call.respondText("Hello from logged in only site ${userSession?.username}," +
//                        "your type is ${userSession?.userType}")
                call.respond(ThymeleafContent("/afterLogin/testowe", mapOf("test" to Testowe(69, "testowe"))))
            }

            get ("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/")
            }
        }
    }
}

data class Testowe(val id: Int, val name: String)