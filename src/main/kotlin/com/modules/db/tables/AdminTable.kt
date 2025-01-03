package com.modules.db.tables

import com.modules.db.other.ConstsDB
import org.jetbrains.exposed.dao.id.IntIdTable

object AdminTable : IntIdTable(ConstsDB.ADMIN) {
    val username = varchar(ConstsDB.USERNAME, 70)
    val password = varchar(ConstsDB.PASSWORD, 70)
    val userType = varchar(ConstsDB.USER_TYPE,20).default(ConstsDB.ADMIN)
}