package com.modules.db.reposInterfaces

import com.modules.db.dataModels.AdminModel

interface AdminInterface {
    suspend fun getByUsername(username: String): AdminModel?

    suspend fun removeByUsername(username: String): Boolean

    suspend fun addRow(newRow: AdminModel): Boolean
}
