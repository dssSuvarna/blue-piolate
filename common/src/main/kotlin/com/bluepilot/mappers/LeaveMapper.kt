package com.bluepilot.mappers

import com.bluepilot.entities.Leave
import com.bluepilot.entities.LeaveApprover
import com.bluepilot.entities.User
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.Role
import com.bluepilot.models.requests.LeaveRequest
import com.bluepilot.models.responses.LeaveResponse
import com.bluepilot.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Date
import java.time.Instant

@Component
class LeaveMapper @Autowired constructor(
    val leaveApproverMapper: LeaveApproverMapper
) {

    fun toEntity(leaveRequest: LeaveRequest, user: User): Leave {
        return Leave(
            user = user,
            status = LeaveStatus.TO_BE_APPROVED,
            leaveType = leaveRequest.leaveType,
            approvalFrom = mutableListOf(),
            appliedDate = Date(Instant.now().toEpochMilli()),
            reason = leaveRequest.reason,
            leaveDates = leaveRequest.leaveDates
        )
    }

    fun toResponse(leave: Leave): LeaveResponse {
        return LeaveResponse(
            leaveId = leave.id,
            status = leave.status,
            leaveDates = leave.leaveDates,
            leaveType = leave.leaveType,
            approvers = leave.approvalFrom.map { leaveApproverMapper.toResponse(it) },
            appliedDate = leave.appliedDate,
            reason = leave.reason
        )
    }
}