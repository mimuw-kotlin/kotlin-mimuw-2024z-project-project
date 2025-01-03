package com.modules

import com.modules.db.repos.AdminRepo
import com.modules.db.repos.PasswordRepo
import com.modules.db.repos.StudentRepo
import com.modules.db.repos.TeacherRepo
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingAdmin(studentRepo: StudentRepo,
                                 teacherRepo: TeacherRepo,
                                 passwordRepo: PasswordRepo,
                                 adminRepo: AdminRepo
) {
    routing {
        authenticate("admin-session") {
            route("/admin") {
                get("/home") {
                    call.respondText("Admin home page")
                }

            }
        }
    }
}

