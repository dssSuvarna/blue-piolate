package com.bluepilot.coreservice.models.responses

import com.bluepilot.entities.LeaveDate
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.bluepilot.models.responses.LeaveApproverResponse
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Date

data class LeavesApprovalResponse(
    @JsonProperty("leaveId")
    val leaveId: Long,
    @JsonProperty("firstName")
    val firstName: String,
    @JsonProperty("designation")
    val designation: String,
    @JsonProperty("empCode")
    val empCode: String,
    @JsonProperty("leaveDates")
    val leaveDates: List<LeaveDate>,
    @JsonProperty("reason")
    val reason: String,
    @JsonProperty("appliedDate")
    val appliedDate: Date,
    @JsonProperty("approvers")
    var approvers: List<LeaveApproverResponse>,
    @JsonProperty("status")
    val status: LeaveStatus,
    @JsonProperty("profilePicture")
    val profilePicture: String?,
    @JsonProperty("leaveType")
    val leaveType: LeaveType
)