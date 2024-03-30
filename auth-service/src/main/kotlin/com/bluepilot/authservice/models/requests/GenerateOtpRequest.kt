package com.bluepilot.authservice.models.requests

import jakarta.validation.constraints.Pattern

data class GenerateOtpRequest(
    @field:Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\$")
    val username: String
)
