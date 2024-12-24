package com.modules.db.DAO

import com.modules.db.tables.StudentsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class StudentsDAO(id: EntityID<Int>) : IntEntity(id) {

    // IntEntityCls represents a collection of rows in a table with an integer primary key.
    // This class provides methods for querying and managing entities in the database.
    companion object : IntEntityClass<StudentsDAO>(StudentsTable)

    // Here we do property delegation to the columns of the table
    // The delegation automatically provides the getter and setter methods
    var index by StudentsTable.index
    var username by StudentsTable.username
    var user_type by StudentsTable.user_type
    var class_nbr by StudentsTable.class_nbr
    var active by StudentsTable.active
}
