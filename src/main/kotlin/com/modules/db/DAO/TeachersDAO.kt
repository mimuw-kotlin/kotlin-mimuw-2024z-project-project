package com.modules.db.DAO

import com.modules.db.tables.TeachersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TeachersDAO(id: EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<TeachersDAO>(TeachersTable)

    var index by TeachersTable.index
    var username by TeachersTable.username
    var user_type by TeachersTable.user_type
    var class_nbr by TeachersTable.class_nbr
    var active by TeachersTable.active
}