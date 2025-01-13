package com.modules

import com.modules.constants.AppConsts
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlin.test.*

 class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        environment {
            config = MapApplicationConfig(
                "session.secretEncryptKey" to "00112233445566778899aabbccddeeff",
                "session.secretSignKey" to "6819b57a326945c1968f45236589"
            )
        }
        application {
            val studentRepo = FakeStudentRepo()
            val teacherRepo = FakeTeacherRepo()
            val passwordRepo = FakePswdRepo()
            val adminRepo = FakeAdminRepo()

            install(Sessions) {
//                val secretEncryptKey = hex(this@module.environment.config.property("session.secretEncryptKey").getString())
//                val secretSignKey = hex(this@module.environment.config.property("session.secretSignKey").getString())

                cookie<UserSession>(AppConsts.USER_SESSION, SessionStorageMemory()) {
                    cookie.path = "/"
                    cookie.maxAgeInSeconds = 240
//                    transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
                }
            }

            install(Authentication) {
                session<UserSession>(AppConsts.BASIC_AUTH_SESSION) {
                    validate { session: UserSession? ->
                        if (session != null) {
                            return@validate session
                        } else {
                            return@validate null
                        }
                    }

                    challenge {
                        call.respondRedirect("/")
                    }
                }
            }
            configureTemplating()
            configureSecurity(passwordRepo, teacherRepo, studentRepo, adminRepo)
            configureRouting(studentRepo, teacherRepo, passwordRepo)
        }

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }

 }
