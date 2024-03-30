package com.bluepilot.mappers

import com.bluepilot.entities.Leave
import com.bluepilot.entities.User
import com.bluepilot.models.responses.LeaveDetailsResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LeaveDetailsMapper @Autowired constructor(
    val leaveMapper: LeaveMapper
) {

    fun toResponse(leaves: List<Leave>, user: User): LeaveDetailsResponse {
        return LeaveDetailsResponse(
            userId = user.id,
            name = user.firstName + " " + user.lastName,
            designation = user.designation,
            totalLeaves = user.userDetails!!.leaveDetails.totalLeaves,
            totalPendingLeaves = user.userDetails!!.leaveDetails.pendingLeaves,
            totalPendingPrivilegeLeaves = user.userDetails!!.leaveDetails.pendingPrivilegeLeave,
            totalPendingSickLeaves = user.userDetails!!.leaveDetails.pendingSickLeave,
            totalPendingCompOffLeaves = user.userDetails!!.leaveDetails.totalCompOffLeave,
            leaveApplied = leaves.map { leaveMapper.toResponse(it) },
            profilePicture = user.profilePicture
        )
    }
}