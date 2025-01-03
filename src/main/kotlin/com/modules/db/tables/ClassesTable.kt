package com.modules.db.tables

import com.modules.db.other.ConstsDB
import org.jetbrains.exposed.dao.id.IntIdTable

object ClassesTable : IntIdTable(ConstsDB.CLASSES){
    val classNbr = varchar(ConstsDB.CLASS_NBR, 3).uniqueIndex()
    val classTeacherName = varchar(ConstsDB.CLASS_TEACHER_NAME, 70).default(ConstsDB.N_A)
}