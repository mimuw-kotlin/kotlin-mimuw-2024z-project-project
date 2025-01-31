package com.modules.db.dao

import com.modules.db.tables.TeachersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TeachersDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TeachersDAO>(TeachersTable)

    var index by TeachersTable.index
    var username by TeachersTable.username
    var userType by TeachersTable.userType
    var classNbr by TeachersTable.classNbr
    var subjectIndex by TeachersTable.subjectIndex
    var active by TeachersTable.active
}
