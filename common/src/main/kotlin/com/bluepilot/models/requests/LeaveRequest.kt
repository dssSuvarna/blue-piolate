package com.bluepilot.models.requests

import com.bluepilot.entities.LeaveDate
import com.bluepilot.enums.LeaveType
import java.sql.Date
import java.time.Instant

data class LeaveRequest(
    val leaveType: LeaveType,
    val reason: String,
    val leaveDates: List<LeaveDate>,
) {
    val appliedDate: Date = Date(Instant.now().toEpochMilli())
    fun getTotalLeaveDays() = leaveDates.sumOf { it.day.value }
}
