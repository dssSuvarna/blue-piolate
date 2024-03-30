package com.bluepilot.mappers

import com.bluepilot.entities.Leave
import com.bluepilot.models.responses.UpcomingLeaveResponse
import org.springframework.stereotype.Component

@Component
class UpcomingUserLeaveResponseMapper {

    fun toResponse(leave: Leave): UpcomingLeaveResponse {
        val user = leave.user
        return UpcomingLeaveResponse(
            name = user.firstName + " " + user.lastName,
            designation = user.designation,
            professionalEmail = user.userDetails!!.professionalEmail,
            leaveDates = leave.leaveDates,
            profilePicture = user.profilePicture
        )
    }
}