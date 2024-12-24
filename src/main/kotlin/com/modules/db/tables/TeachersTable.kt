package com.modules.db.tables

import com.modules.db.other.ConstsDB
import com.modules.db.other.UserTypes
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object TeachersTable : IntIdTable(ConstsDB.TEACHERS){
    val index = varchar(ConstsDB.INDEX, 8).uniqueIndex()
    val username = varchar(ConstsDB.USERNAME, 70)
    val user_type = varchar(ConstsDB.USER_TYPE, 20).default(UserTypes.getType(ConstsDB.TEACHER))
    val class_nbr = varchar(ConstsDB.CLASS_NBR, 3).references(ClassesTable.class_nbr,
        onDelete = ReferenceOption.CASCADE)
    val active = bool(ConstsDB.ACTIVE).default(false)
}