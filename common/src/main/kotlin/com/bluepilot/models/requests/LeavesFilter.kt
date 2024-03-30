package com.bluepilot.models.requests

import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType

data class LeavesFilter(
    val status: LeaveStatus? = null,
    val leaveType: LeaveType? = null,
    val year: Int? = null
)