package com.modules.db.tables

import com.modules.db.other.ConstsDB
import org.jetbrains.exposed.dao.id.IntIdTable

object SubjectsTable : IntIdTable(ConstsDB.SUBJECTS){
    val subjectIndex = varchar(ConstsDB.SUBJECT_INDEX, 10).uniqueIndex()
    val subjectName = varchar(ConstsDB.SUBJECT_NAME, 70)
    val description = varchar(ConstsDB.DESCRIPTION, 555).default(ConstsDB.NO_DESCR_AVAILABLE)
}