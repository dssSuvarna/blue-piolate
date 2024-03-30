package com.bluepilot.coreservice.models.requests

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class UpdateUserResourceRequest(
    @field:NotNull
    val userId: Long,
    @field:NotNull
    val systemResourceId: Long,
    @field:NotNull
    val idCard: String,
    @field:Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\$")
    val professionalEmail: String
)