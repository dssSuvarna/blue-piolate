package com.bluepilot.userservice.services

import com.bluepilot.entities.LeaveApprover
import com.bluepilot.entities.User
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.Role
import com.bluepilot.errors.ErrorMessages
import com.bluepilot.errors.NotAllowed
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.Validator.Companion.validate
import com.bluepilot.mappers.LeaveDetailsMapper
import com.bluepilot.mappers.LeaveMapper
import com.bluepilot.mappers.LeaveSpecification
import com.bluepilot.models.requests.LeaveRequest
import com.bluepilot.models.requests.LeavesFilter
import com.bluepilot.models.responses.LeaveDetailsResponse
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.LeaveRepository
import com.bluepilot.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.sql.Date
import java.time.Instant

@Service
@Transactional
class LeaveService @Autowired constructor(
    val leaveMapper: LeaveMapper,
    val leaveRepository: LeaveRepository,
    val leaveDetailsMapper: LeaveDetailsMapper,
    val eventService: EventService,
    val userRepository: UserRepository,
) {
    fun applyLeave(leaveRequest: LeaveRequest, user: User) {
        validateLeaveRequest(leaveRequest, user)
        val leave = leaveMapper.toEntity(leaveRequest, user)
        leave.approvalFrom.addAll(getLeaveApprovers(user))
        val approversEmail = leaveRepository.save(leave)
            .approvalFrom.filterNot { it.user.authUser.role.name in listOf(Role.HR, Role.ADMIN) }
            .map { it.user.userDetails!!.professionalEmail }
        eventService.sendEvent(
            notificationEventType = NotificationEventType.LEAVE_APPLIED_APPLICATION,
            additionalData = mapOf(
                "emailTo" to approversEmail.joinToString(", "),
                "name" to user.firstName + " " + user.lastName,
                "fromDate" to leaveRequest.leaveDates.first().date.toString(),
                "toDate" to leaveRequest.leaveDates.last().date.toString(),
                "totalLeaves" to leaveRequest.getTotalLeaveDays().toString(),
                "leaveType" to leaveRequest.leaveType.getLabel()
            )
        )
    }

    fun getLeaveApprovers(user: User): List<LeaveApprover> {
        val approvers = mutableListOf(
            LeaveApprover(
                user = userRepository.findUserByAuthUser_Role_Name(Role.HR).first(),
                status = LeaveStatus.TO_BE_APPROVED
            ),
            LeaveApprover(
                user = userRepository.findUserByAuthUser_Role_Name(Role.ADMIN).first(),
                status = LeaveStatus.TO_BE_APPROVED
            )
        )
        return when (user.reporter!!.authUser.role.name) {
            Role.HR, Role.ADMIN -> {
                approvers
            }

            else -> {
                approvers.add(LeaveApprover(user = user.reporter!!, status = LeaveStatus.TO_BE_APPROVED))
                approvers
            }
        }
    }

    fun validateLeaveRequest(leaveRequest: LeaveRequest, user: User) {
        val leaveDetails = user.userDetails!!.leaveDetails
        val currentYear = Date(Instant.now().toEpochMilli()).toLocalDate().year
        val leaveAppliedIsNextYear = leaveRequest.leaveDates.any { currentYear < it.date.toLocalDate().year }
        val existingLeave = leaveRepository.findByUserIdAndStatus(user.id, LeaveStatus.APPROVED).any { leave ->
            leaveRequest.leaveDates.any { leave.leaveDates.map { it.date }.contains(it.date) }
        }
        validate(
            leaveAppliedIsNextYear,
            NotAllowedException(NotAllowed(message = ErrorMessages.COULD_NOT_APPLY_LEAVE_FOR_NEXT_YEAR))
        )
        validate(
            existingLeave,
            NotAllowedException(NotAllowed(message = ErrorMessages.COULD_NOT_APPLY_MULTIPLE_LEAVE))
        )
        validate(
            leaveRequest.leaveType == LeaveType.LOP && leaveDetails.pendingPrivilegeLeave > BigDecimal(0.0),
            NotAllowedException(NotAllowed(message = ErrorMessages.COULD_NOT_APPLY_LOP))
        )
        val balanceLeave = when (leaveRequest.leaveType) {
            LeaveType.PRIVILEGE_LEAVE -> {
                leaveDetails.pendingPrivilegeLeave
            }

            LeaveType.SICK_LEAVE -> {
                leaveDetails.pendingSickLeave
            }

            LeaveType.COMPENSATORY_OFF -> {
                leaveDetails.totalCompOffLeave
            }

            else -> {
                Int.MAX_VALUE.toBigDecimal()
            }
        }
        validate(
            balanceLeave < leaveRequest.getTotalLeaveDays(),
            NotAllowedException(NotAllowed(message = ErrorMessages.INSUFFICIENT_LEAVE_BALANCE))
        )
    }

    fun getLeaveSummary(user: User, leavesFilter: LeavesFilter): LeaveDetailsResponse {
        val specification = LeaveSpecification.withFilter(user, leavesFilter)
        val leaves = leaveRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "appliedDate"))
        return leaveDetailsMapper.toResponse(leaves, user)
    }
}