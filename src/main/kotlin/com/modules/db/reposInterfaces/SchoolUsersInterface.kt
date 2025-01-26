package com.modules.db.reposInterfaces

interface SchoolUsersInterface<ModelT> {
    suspend fun getAll(): List<ModelT>

    suspend fun getByIndex(index: String): ModelT?

    suspend fun removeByIndex(index: String): Boolean

    suspend fun addRow(newRow: ModelT): Boolean

    suspend fun getByClassNbr(clsNbr: String): List<ModelT>

    suspend fun getByUsername(username: String): ModelT?

    suspend fun updateRow(
        index: String,
        username: String,
        userType: String,
        classNbr: String,
        subjectIndex: String,
        active: Boolean,
    ): Unit

    suspend fun toggleActiveByIndex(index: String): Boolean
}
