package com.modules.db.dao

import com.modules.db.tables.AdminTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AdminDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object CompObj : IntEntityClass<AdminDAO>(AdminTable)

    var username by AdminTable.username
    var userType by AdminTable.userType
}
