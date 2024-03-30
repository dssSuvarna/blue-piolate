package com.bluepilot.models.requests

import com.bluepilot.enums.LeaveStatus

data class LeavesApprovalFilter(
    val status: LeaveStatus? = null
)