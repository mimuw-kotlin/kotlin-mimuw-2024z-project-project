package com.modules.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import com.modules.db.other.ConstsDB

object PasswordsTable : IntIdTable(ConstsDB.PASSWORDS){
    val username = varchar(ConstsDB.USERNAME, 70)
    val password = varchar(ConstsDB.PASSWORD, 70)
}