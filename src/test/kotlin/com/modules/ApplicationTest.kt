package com.modules

import com.modules.constants.AppConsts
import io.ktor.client.*
import io.ktor.client.plugins.*
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
import io.ktor.server.testing.client.*
import kotlin.test.*

const val USERNAME = "username"
const val PASSWORD = "password"

class ApplicationTest {
    @Test
    fun testRegister() =
        testApplication {
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

            val bodyLst = mutableListOf(USERNAME to "test", PASSWORD to "test")

            // ----- REGISTER STUDENT -----

            // registration successful
            var response =
                client.post("/register/student") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.OK, response.status)

            // empty username
            bodyLst[0] = USERNAME to ""
            response =
                client.post("/register/student") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // empty username and password
            bodyLst[1] = PASSWORD to ""
            response =
                client.post("/register/student") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // empty password
            bodyLst[0] = USERNAME to "test"
            bodyLst[1] = PASSWORD to ""
            response =
                client.post("/register/student") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // Too short username
            bodyLst[0] = USERNAME to "t"
            bodyLst[1] = PASSWORD to "test"
            response =
                client.post("/register/student") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // Too short password
            bodyLst[0] = USERNAME to "test"
            bodyLst[1] = PASSWORD to "t"
            response =
                client.post("/register/student") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // null username
            bodyLst.removeAt(0)
            response =
                client.post("/register/student") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // null password
            bodyLst[0] = USERNAME to "test"
            response =
                client.post("/register/student") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // ----- REGISTER TEACHER -----
            bodyLst[0] = USERNAME to "test"
            bodyLst.add(PASSWORD to "test")

            // registration successful
            response =
                client.post("/register/teacher") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.OK, response.status)

            // empty username
            bodyLst[0] = USERNAME to ""
            response =
                client.post("/register/teacher") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // empty username and password
            bodyLst[1] = PASSWORD to ""
            response =
                client.post("/register/teacher") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // empty password
            bodyLst[0] = USERNAME to "test"
            bodyLst[1] = PASSWORD to ""
            response =
                client.post("/register/teacher") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // Too short username
            bodyLst[0] = USERNAME to "t"
            bodyLst[1] = PASSWORD to "test"
            response =
                client.post("/register/teacher") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // Too short password
            bodyLst[0] = USERNAME to "test"
            bodyLst[1] = PASSWORD to "t"
            response =
                client.post("/register/teacher") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // null username
            bodyLst.removeAt(0)
            response =
                client.post("/register/teacher") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)

            // null password
            bodyLst[0] = USERNAME to "test"
            response =
                client.post("/register/teacher") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }

    @Test
    fun testLogin() =
        testApplication {
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
                install(ContentNegotiation) {
                    json()
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

            val bodyLst = mutableListOf(USERNAME to "admin", PASSWORD to "admin")

            // ----- LOGIN ADMIN-----
            var response =
                client.post("/login") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.Found, response.status)

            // ----- LOGIN TEACHER not active-----
            bodyLst[0] = USERNAME to "teacher"
            bodyLst[1] = PASSWORD to "teacher"

            response =
                client.post("/login") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            // when is not active we do the redirection thus status is Found
            assertEquals(HttpStatusCode.Found, response.status)

            // ----- LOGIN STUDENT active-----
            bodyLst[0] = USERNAME to "student1"
            bodyLst[1] = PASSWORD to "student1"

            response =
                client.post("/login") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.OK, response.status)

            // ----- LOGIN wrong parameters-----

            // wrong username
            bodyLst[0] = USERNAME to "stud"
            bodyLst[1] = PASSWORD to "student1"

            response =
                client.post("/login") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.Found, response.status)

            // wrong password
            bodyLst[0] = USERNAME to "student1"
            bodyLst[1] = PASSWORD to "stud"

            response =
                client.post("/login") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(bodyLst.toList().formUrlEncode())
                }
            assertEquals(HttpStatusCode.Found, response.status)

            // Login form invalid status
            response =
                client.get("/loginForm") {
                    url {
                        parameters[AppConsts.SESSION] = AppConsts.INVALID_CRED
                    }
                }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
}
