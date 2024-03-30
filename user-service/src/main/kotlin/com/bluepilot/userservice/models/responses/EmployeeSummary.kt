package com.bluepilot.userservice.models.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class EmployeeSummary(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("firstName")
    val firstName: String,
    @JsonProperty("lastName")
    val lastName: String,
    @JsonProperty("employeeCode")
    val employeeCode: String,
    @JsonProperty("email")
    val email: String?,
    @JsonProperty("designation")
    val designation: String,
    @JsonProperty("trainer")
    val trainer: Long?,
    @JsonProperty("profilePicture")
    val profilePicture: String?,
)
