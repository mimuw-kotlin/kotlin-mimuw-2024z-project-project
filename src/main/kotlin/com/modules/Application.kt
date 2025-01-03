package com.modules

import com.modules.db.repos.AdminRepo
import com.modules.db.repos.PasswordRepo
import com.modules.db.repos.StudentRepo
import com.modules.db.repos.TeacherRepo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val studentRepo = StudentRepo()
    val teacherRepo = TeacherRepo()
    val passwordRepo = PasswordRepo()
    val adminRepo = AdminRepo()

    install(Sessions) {
        val secretEncryptKey = hex(this@module.environment.config.property("session.secretEncryptKey").getString())
        val secretSignKey = hex(this@module.environment.config.property("session.secretSignKey").getString())

        cookie<UserSession>("user_session", SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 30
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }

    install(Authentication) {

        session<UserSession>("auth-session") {
            validate { session: UserSession? ->
                if (session != null)
                    return@validate session
                else
                    return@validate null
            }

            challenge {
                call.respondRedirect("/")
            }
        }
    }

    configureSockets()
    configureSerialization()
    configureDatabases(environment.config)
    configureTemplating()
    configureHTTP()
    configureSecurity(passwordRepo, teacherRepo, studentRepo, adminRepo)
    configureRouting(studentRepo, teacherRepo, passwordRepo, adminRepo)
}
