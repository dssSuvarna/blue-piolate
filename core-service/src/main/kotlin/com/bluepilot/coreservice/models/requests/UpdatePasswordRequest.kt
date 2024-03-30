package com.bluepilot.coreservice.models.requests

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class UpdatePasswordRequest(
    @field:NotNull
    val oldPassword: String,
    @field:Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    val newPassword: String
)
