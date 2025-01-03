package com.modules.db.DAO

import com.modules.db.tables.ClassesTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ClassesDAO (id: EntityID<Int>) : IntEntity(id){

    companion object : IntEntityClass<ClassesDAO>(ClassesTable)

    val class_nbr by ClassesTable.classNbr
    val class_teacher_name by ClassesTable.classTeacherName
}