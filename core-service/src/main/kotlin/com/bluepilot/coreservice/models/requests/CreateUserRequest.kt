package com.bluepilot.coreservice.models.requests

import com.bluepilot.enums.Role
import jakarta.validation.constraints.Pattern
import java.sql.Date

data class CreateUserRequest(
    val onboardingContextId: Long,
    @field:Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\$")
    val professionalEmail: String,
    val role: Role,
    val designation: String,
    val dateOfJoining: Date,
    val reporterId: Long? = null
)