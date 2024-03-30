package com.bluepilot.coreservice.models.responses

import com.bluepilot.enums.UserStatus
import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse (
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("employeeCode")
    val employeeCode: String,
    @JsonProperty("firstName")
    val firstName: String,
    @JsonProperty("lastName")
    val lastName: String,
    @JsonProperty("designation")
    val designation: String,
    @JsonProperty("status")
    val status: UserStatus,
    @JsonProperty("professionalEmail")
    val professionalEmail: String,
    @JsonProperty("profilePicture")
    val profilePicture: String?
)
