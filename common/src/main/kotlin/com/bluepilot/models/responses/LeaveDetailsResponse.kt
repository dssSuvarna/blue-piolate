package com.bluepilot.models.responses

import com.bluepilot.entities.LeaveDate
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.sql.Date

data class LeaveDetailsResponse(
    @JsonProperty("userId")
    val userId: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("designation")
    val designation: String,
    @JsonProperty("totalLeaves")
    val totalLeaves: BigDecimal,
    @JsonProperty("totalPendingLeaves")
    val totalPendingLeaves: BigDecimal,
    @JsonProperty("totalPendingPrivilegeLeaves")
    val totalPendingPrivilegeLeaves: BigDecimal,
    @JsonProperty("totalPendingSickLeaves")
    val totalPendingSickLeaves: BigDecimal,
    @JsonProperty("totalPendingCompOffLeaves")
    val totalPendingCompOffLeaves: BigDecimal,
    @JsonProperty("leaveApplied")
    val leaveApplied: List<LeaveResponse>,
    @JsonProperty("profilePicture")
    val profilePicture: String?
)

data class LeaveResponse(
    @JsonProperty("leaveId")
    val leaveId: Long,
    @JsonProperty("leaveDates")
    val leaveDates: List<LeaveDate>,
    @JsonProperty("status")
    var status: LeaveStatus,
    @JsonProperty("leaveType")
    val leaveType: LeaveType,
    @JsonProperty("approvers")
    var approvers: List<LeaveApproverResponse>,
    @JsonProperty("appliedDate")
    val appliedDate: Date,
    @JsonProperty("reason")
    val reason: String
)

data class LeaveApproverResponse(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("employeeCode")
    val employeeCode: String,
    @JsonProperty("designation")
    val designation: String,
    @JsonProperty("status")
    val status: LeaveStatus,
    @JsonProperty("profilePicture")
    val profilePicture: String? = null,
)