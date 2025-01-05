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
import io.ktor.server.thymeleaf.*
import kotlin.reflect.typeOf

fun Application.configureRoutingTeacher(studentRepo: StudentRepo,
                                      teacherRepo: TeacherRepo,
                                      passwordRepo: PasswordRepo,
                                      classRepo: ClassRepo
) {
    routing {
        authenticate(AppConsts.TEACHER_SESSION) {

        }
    }
}


