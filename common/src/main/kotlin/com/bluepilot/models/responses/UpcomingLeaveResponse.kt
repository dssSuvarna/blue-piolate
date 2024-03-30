package com.bluepilot.models.responses

import com.bluepilot.entities.LeaveDate
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Date

data class UpcomingLeaveResponse(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("designation")
    val designation: String? = null,
    @JsonProperty("professionalEmail")
    val professionalEmail: String?,
    @JsonProperty("leaveDates")
    val leaveDates: List<LeaveDate>,
    @JsonProperty("profilePicture")
    val profilePicture: String?
)