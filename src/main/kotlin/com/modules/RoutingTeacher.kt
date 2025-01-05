package com.modules

import com.modules.constants.AppConsts
import com.modules.db.other.UserTypes
import com.modules.db.repos.*
import com.modules.utils.checkEditUserParams
import com.modules.utils.checkUserType
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*
import kotlin.reflect.typeOf

fun Application.configureRoutingTeacher(studentRepo: StudentRepo,
                                      teacherRepo: TeacherRepo,
                                      passwordRepo: PasswordRepo,
                                      classRepo: ClassRepo
) {
    routing {
        authenticate(AppConsts.TEACHER_SESSION) {
            route("/teacher") {

                get("/home") {
                    val session = call.sessions.get<UserSession>()
                    val teacher = teacherRepo.getByUsername(session!!.username)

                    if (teacher == null)
                    {
                        call.sessions.clear<UserSession>()
                        call.respond(ThymeleafContent("beforeLogin/loginForm", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
                        return@get
                    }
                    call.respond(ThymeleafContent("teacher/controlPanel", mapOf(AppConsts.TEACHER to teacher)))
                    return@get
                }

                get("/classes") {
                    val allClasses = classRepo.getAll()
                    val params = call.request.queryParameters
                    if (params[AppConsts.STATUS] == null)
                    {
                        call.respond(ThymeleafContent("teacher/classes", mapOf(AppConsts.CLASSES to allClasses)))
                        return@get
                    }

                    call.respond(ThymeleafContent("teacher/classes", mapOf(AppConsts.CLASSES to allClasses, AppConsts.STATUS to params[AppConsts.STATUS]!!)))
                }

                post("/takeClass") {
                    val params = call.receiveParameters()
                    val classNbr = params[AppConsts.CLASS_NBR]
                    val session = call.sessions.get<UserSession>()
                    val teacher = teacherRepo.getByUsername(session!!.username)

                    if (teacher == null || classNbr == null)
                    {
                        call.sessions.clear<UserSession>()
                        call.respond(ThymeleafContent("beforeLogin/loginForm", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
                        return@post
                    }

                    if (teacher.classNbr == AppConsts.N_A)
                    {
                        val classObj = classRepo.getByClassNbr(classNbr)
                        if (classObj == null)
                        {
                            call.respondRedirect("/teacher/classes?" + AppConsts.STATUS + AppConsts.EQUALS + "classNotFound")
                            return@post
                        }

                        if (classObj.classTeacherName != null)
                        {
                            call.respondRedirect("/teacher/classes?" + AppConsts.STATUS + AppConsts.EQUALS + "classAlreadyTaken")
                            return@post
                        }

                        classRepo.updateRow(classNbr, teacher.username)
                        teacherRepo.updateRow(teacher.index, teacher.username, teacher.userType, classNbr, teacher.subjectIndex, teacher.active)
                        call.respondRedirect("/teacher/classes?" + AppConsts.STATUS + AppConsts.EQUALS + "success")
                        return@post
                    }

                    call.respondRedirect("/teacher/classes?" + AppConsts.STATUS + AppConsts.EQUALS + "alreadyHasClass")
                    return@post
                }

                post("/leaveClass") {
                    val session = call.sessions.get<UserSession>()
                    val teacher = teacherRepo.getByUsername(session!!.username)

                    if (teacher == null)
                    {
                        call.sessions.clear<UserSession>()
                        call.respond(ThymeleafContent("beforeLogin/loginForm", mapOf(AppConsts.SESSION to AppConsts.INVALID_CRED)))
                        return@post
                    }

                    if (teacher.classNbr == AppConsts.N_A)
                    {
                        call.respondRedirect("/teacher/classes?" + AppConsts.STATUS + AppConsts.EQUALS + "noClass")
                        return@post
                    }

                    classRepo.updateRow(teacher.classNbr, null)
                    teacherRepo.updateRow(teacher.index, teacher.username, teacher.userType, AppConsts.N_A, teacher.subjectIndex, teacher.active)
                    call.respondRedirect("/teacher/classes?" + AppConsts.STATUS + AppConsts.EQUALS + "success")
                    return@post
                }

                get("/showTeachers") {
                    val allTeachers = teacherRepo.getAll()
                    call.respond(ThymeleafContent("teacher/showTeachers", mapOf(AppConsts.TEACHERS to allTeachers)))
                    return@get
                }

                get("/showStudents") {
                    val session = call.sessions.get<UserSession>()
                    val teacher = teacherRepo.getByUsername(session!!.username)

                    if (teacher == null)
                    {
                        call.sessions.clear<UserSession>()
                        call.respondRedirect("/")
                        return@get
                    }

                    val allStudents = studentRepo.getStudentsFromGivenClass(teacher.classNbr)
                    call.respond(ThymeleafContent("teacher/showStudents", mapOf(AppConsts.STUDENTS to allStudents,
                        AppConsts.CLASS_NBR to teacher.classNbr)))
                    return@get
                }
            }
        }
    }
}


