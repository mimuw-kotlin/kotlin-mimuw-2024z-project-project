package com.modules

import com.modules.constants.AppConsts
import com.modules.db.repos.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*

fun Application.configureRoutingStudent(studentRepo: StudentRepo,
                                        teacherRepo: TeacherRepo,
                                        classRepo: ClassRepo
) {
    routing {
        authenticate(AppConsts.STUDENT_SESSION) {
            route("/student") {

                get("/home") {
                    val session = call.sessions.get<UserSession>()
                    val student = studentRepo.getByUsername(session!!.username)

                    if (student == null)
                    {
                        call.sessions.clear<UserSession>()
                        call.respond(ThymeleafContent("beforeLogin/loginForm", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
                        return@get
                    }
                    call.respond(ThymeleafContent("student/home", mapOf(AppConsts.STUDENT to student)))
                    return@get
                }

                get("/showStudents") {
                    val session = call.sessions.get<UserSession>()
                    val student = studentRepo.getByUsername(session!!.username)

                    if (student == null)
                    {
                        call.sessions.clear<UserSession>()
                        call.respondRedirect("/")
                        return@get
                    }

                    val allStudents = studentRepo.getStudentsFromGivenClass(student.classNbr)
                    val teacher = classRepo.getTeacherFromClass(student.classNbr)

                    if (teacher == null)
                    {
                        call.respond(ThymeleafContent("student/showStudents", mapOf(AppConsts.STUDENTS to allStudents,
                            AppConsts.CLASS_NBR to student.classNbr, AppConsts.TEACHER to AppConsts.N_A)))
                        return@get
                    }

                    call.respond(ThymeleafContent("student/showStudents", mapOf(AppConsts.STUDENTS to allStudents,
                        AppConsts.CLASS_NBR to student.classNbr, AppConsts.TEACHER to teacher.username)))
                    return@get
                }
            }
        }
    }
}


