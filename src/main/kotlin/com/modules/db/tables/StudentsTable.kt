package com.modules.db.tables

import com.modules.db.other.ConstsDB
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

// A DAO (Data Access Object) is a design pattern that provides an
// abstract interface to some type of database
// This is database table for tasks, it's an object
// because we need only one database table for tasks
// IntIdTable is a class that represents a table with an integer primary key
object StudentsTable : IntIdTable(ConstsDB.STUDENTS) {
    val index = varchar(ConstsDB.INDEX, 8).uniqueIndex()
    val username = varchar(ConstsDB.USERNAME, 70)
    val userType = varchar(ConstsDB.USER_TYPE, 20).default(ConstsDB.STUDENT)
    val classNbr =
        varchar(ConstsDB.CLASS_NBR, 3).references(
            ClassesTable.classNbr,
            onDelete = ReferenceOption.CASCADE,
        )
    val active = bool(ConstsDB.ACTIVE).default(false)
}
