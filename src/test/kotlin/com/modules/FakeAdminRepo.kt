package com.modules

import com.modules.db.dataModels.AdminModel
import com.modules.db.repos.AdminRepo
import com.modules.db.reposInterfaces.AdminInterface

class FakeAdminRepo : AdminInterface {

    private val admins = mapOf(
        "admin" to AdminModel("admin", "admin"),
    )

    override suspend fun getByUsername(username: String) : AdminModel? {
        return admins[username]
    }
    override suspend fun removeByUsername(username: String) = false
    override suspend fun addRow(newRow: AdminModel) = false
}