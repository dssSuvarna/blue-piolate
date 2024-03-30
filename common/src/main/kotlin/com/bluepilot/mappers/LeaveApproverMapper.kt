package com.bluepilot.mappers

import com.bluepilot.entities.LeaveApprover
import com.bluepilot.models.responses.LeaveApproverResponse
import org.springframework.stereotype.Component


@Component
class LeaveApproverMapper {
    fun toResponse(leaveApprover: LeaveApprover): LeaveApproverResponse {
        return LeaveApproverResponse(
            name = leaveApprover.user.firstName + " " + leaveApprover.user.lastName,
            employeeCode = leaveApprover.user.employeeCode,
            designation = leaveApprover.user.designation,
            status = leaveApprover.status,
            profilePicture = leaveApprover.user.profilePicture
        )
    }
}