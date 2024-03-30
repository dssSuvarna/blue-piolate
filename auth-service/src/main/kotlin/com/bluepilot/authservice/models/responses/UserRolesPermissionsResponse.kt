package com.bluepilot.authservice.models.responses

import com.bluepilot.models.RolePermission
import com.fasterxml.jackson.annotation.JsonProperty

data class UserRolesPermissionsResponse(
    @JsonProperty("userId")
    val userId: Long,
    @JsonProperty("rolePermissions")
    val rolePermissions: RolePermission
)