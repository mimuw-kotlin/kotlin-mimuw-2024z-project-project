package com.modules.db.DAO

import com.modules.db.tables.PasswordsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PasswordsDAO (id: EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<PasswordsDAO>(PasswordsTable)

    var username by PasswordsTable.username
    var password by PasswordsTable.password
}