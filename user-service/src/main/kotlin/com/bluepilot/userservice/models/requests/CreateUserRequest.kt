package com.bluepilot.userservice.models.requests

import com.bluepilot.enums.Role

data class CreateUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: Long,
    val role: Role,
    val userName: String
)
