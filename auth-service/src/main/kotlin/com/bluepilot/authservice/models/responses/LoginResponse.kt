package com.bluepilot.authservice.models.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginResponse(
    @JsonProperty("token")
    val token: String
)