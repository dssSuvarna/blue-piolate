package com.bluepilot.coreservice.services

import com.bluepilot.coreservice.models.responses.LeavesApprovalResponse
import com.bluepilot.entities.Leave
import com.bluepilot.entities.LeaveApprover
import com.bluepilot.entities.User
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.Role
import com.bluepilot.errors.ErrorMessages
import com.bluepilot.errors.NotAllowed
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.Validator
import com.bluepilot.mappers.LeaveApproverMapper
import com.bluepilot.mappers.LeaveDetailsMapper
import com.bluepilot.mappers.LeaveSpecification
import com.bluepilot.mappers.UpcomingUserLeaveResponseMapper
import com.bluepilot.models.requests.LeavesApprovalFilter
import com.bluepilot.models.responses.LeaveDetailsResponse
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.models.responses.UpcomingLeaveResponse
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.LeaveRepository
import com.bluepilot.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.sql.Date
import java.time.Duration
import java.time.Instant
import java.time.YearMonth
import java.time.ZonedDateTime

@Service
@Transactional
class LeaveService @Autowired constructor(
    val leaveRepository: LeaveRepository,
    val leaveDetailsMapper: LeaveDetailsMapper,
    val userRepository: UserRepository,
    val upcomingUserLeaveResponseMapper: UpcomingUserLeaveResponseMapper,
    val leaveApproverMapper: LeaveApproverMapper,
    val eventService: EventService
) {

    fun approveOrRejectLeave(leaveId: Long, status: LeaveStatus, user: User) {
        val leave = leaveRepository.findById(leaveId).get()
        val isRejected = leave.approvalFrom.firstOrNull { it.status == LeaveStatus.REJECTED } != null
        Validator.validate(
            isRejected,
            NotAllowedException(NotAllowed(message = "Can't $status leave already Rejected"))
        )
        val approver = leave.approvalFrom.first { it.user == user }
        Validator.validate(
            (approver.status == LeaveStatus.APPROVED || approver.status == LeaveStatus.REJECTED),
            NotAllowedException(NotAllowed(message = "already $status"))
        )
        approver.status = status
        if (status == LeaveStatus.REJECTED) {
            leave.status = LeaveStatus.REJECTED
            eventService.sendEvent(
                notificationEventType = NotificationEventType.LEAVE_REJECTED,
                additionalData = mapOf(
                    "emailTo" to leave.user.userDetails!!.professionalEmail,
                    "fromDate" to leave.leaveDates.first().toString(),
                    "toDate" to leave.leaveDates.last().toString(),
                    "approvers" to leave.approvalFrom.filter { it.status == LeaveStatus.REJECTED }
                        .joinToString("") { "<li>${it.user.firstName +" "+ it.user.lastName}</li>" }
                )
            )
        } else {
            validateLeaveBalance(leave)
            approveLeave(leave, approver, status)
        }
        updateLeaveDetails(leave, leave.user)
    }

    private fun updateLeaveDetails(leave: Leave, user: User) {
        if (leave.status == LeaveStatus.APPROVED) {
            when (leave.leaveType) {
                LeaveType.PRIVILEGE_LEAVE -> {
                    user.userDetails!!.leaveDetails.pendingLeaves -= leave.getTotalLeaveDays()
                    user.userDetails!!.leaveDetails.pendingPrivilegeLeave -= leave.getTotalLeaveDays()
                }

                LeaveType.SICK_LEAVE -> {
                    user.userDetails!!.leaveDetails.pendingLeaves -= leave.getTotalLeaveDays()
                    user.userDetails!!.leaveDetails.pendingSickLeave -= leave.getTotalLeaveDays()
                }

                LeaveType.COMPENSATORY_OFF -> {
                    user.userDetails!!.leaveDetails.pendingLeaves -= leave.getTotalLeaveDays()
                    user.userDetails!!.leaveDetails.totalCompOffLeave -= leave.getTotalLeaveDays()
                }

                else -> {}
            }
            user.userDetails!!.leaveDetails.appliedLeaves += leave.getTotalLeaveDays()
        }
    }

    private fun approveLeave(leave: Leave, approver: LeaveApprover, status: LeaveStatus) {
        if (approver.user.getUserRole() == Role.ADMIN) {
            leave.status = status
        } else {
            val shouldBeApprove = leave.approvalFrom.filter { it.status == LeaveStatus.APPROVED }.size >= 2
            if (shouldBeApprove) {
                leave.status = status
            }
        }

        if (leave.status == LeaveStatus.APPROVED) {
            eventService.sendEvent(
                notificationEventType = NotificationEventType.LEAVE_APPROVED,
                additionalData = mapOf(
                    "emailTo" to leave.user.userDetails!!.professionalEmail,
                    "fromDate" to leave.leaveDates.first().date.toString(),
                    "toDate" to leave.leaveDates.last().date.toString(),
                    "totalLeaves" to leave.getTotalLeaveDays().toString(),
                    "leaveType" to leave.leaveType.getLabel()
                )
            )
        }
    }

    fun validateLeaveBalance(leave: Leave) {
        val leaveDetails = leave.user.userDetails!!.leaveDetails
        val balanceLeave = when (leave.leaveType) {
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
        try {
            Validator.validate(
                balanceLeave < leave.getTotalLeaveDays(),
                NotAllowedException(NotAllowed(message = ErrorMessages.INSUFFICIENT_LEAVE_BALANCE_REJECTED))
            )
        } catch (e: NotAllowedException) {
            leave.status = LeaveStatus.REJECTED
            throw e
        }
    }

    fun getLeavesApprovalsWithFilter(
        user: User,
        pageNumber: Int,
        pageSize: Int,
        leavesApprovalFilter: LeavesApprovalFilter
    ): PageResponse<LeavesApprovalResponse> {
        val specification = LeaveSpecification.withFilter(user, leavesApprovalFilter)
        val sortByAppliedDate = Sort.by(Sort.Order.desc("appliedDate"))
        val pageResponse = leaveRepository
            .findAll(specification, PageRequest.of(pageNumber, pageSize, sortByAppliedDate))
        return getLeavesResponse(pageResponse)
    }

    fun getLeavesResponse(pageOfLeaves: Page<Leave>): PageResponse<LeavesApprovalResponse> {
        return PageResponse(
            totalCount = pageOfLeaves.totalElements,
            pageNumber = pageOfLeaves.pageable.pageNumber,
            pageSize = pageOfLeaves.size,
            currentPageSize = pageOfLeaves.pageable.pageSize,
            contents = pageOfLeaves.content.map {
                LeavesApprovalResponse(
                    leaveId = it.id,
                    firstName = it.user.firstName,
                    designation = it.user.designation,
                    empCode = it.user.employeeCode,
                    leaveDates = it.leaveDates,
                    reason = it.reason,
                    appliedDate = it.appliedDate,
                    approvers = it.approvalFrom.map { leaveApproverMapper.toResponse(it) },
                    leaveType = it.leaveType,
                    status = it.status,
                    profilePicture = it.user.profilePicture
                )
            }
        )
    }

    fun getLeaveSummaryOfAllEmployee(pageNumber: Int, pageSize: Int): PageResponse<LeaveDetailsResponse> {
        val usersWithPagination =
            userRepository.findAllByAuthUser_Role_NameNotIn(
                listOf(Role.ADMIN, Role.HR),
                PageRequest.of(pageNumber, pageSize)
            )
        val userLeaveMap = leaveRepository.findAllByUserIn(usersWithPagination.content).groupBy { it.user }
        return PageResponse(
            totalCount = usersWithPagination.totalElements,
            pageNumber = usersWithPagination.pageable.pageNumber,
            pageSize = usersWithPagination.size,
            currentPageSize = usersWithPagination.numberOfElements,
            contents = userLeaveMap.map { leaveDetailsMapper.toResponse(it.value, it.key) }
        )
    }

    fun getUpcomingLeavesOfEmployees(
        pageNumber: Int,
        pageSize: Int,
        dateRange: Long
    ): PageResponse<UpcomingLeaveResponse> {
        val leavesWithPagination = leaveRepository.findLeavesByDateRange(
            fromDate = Date(Instant.now().toEpochMilli()),
            toDate = Date(Instant.now().plus(Duration.ofDays(dateRange + 1L)).toEpochMilli()),
            pageable = PageRequest.of(pageNumber, pageSize)
        )
        return PageResponse(
            totalCount = leavesWithPagination.totalElements,
            pageNumber = leavesWithPagination.pageable.pageNumber,
            pageSize = leavesWithPagination.size,
            currentPageSize = leavesWithPagination.numberOfElements,
            contents = leavesWithPagination.content.map { upcomingUserLeaveResponseMapper.toResponse(it) }
        )
    }

    fun getLopLeavesOfUser(user: User): BigDecimal {
        val preMonthStartZone = ZonedDateTime.now().minusMonths(1).withDayOfMonth(1)
        val preMonthStartDate = Date(preMonthStartZone.toInstant().toEpochMilli())
        val preMonthEndDate = Date(
            preMonthStartZone.withDayOfMonth(
                YearMonth.of(preMonthStartZone.year, preMonthStartZone.month).lengthOfMonth()
            ).toInstant().toEpochMilli()
        )
        val userLeaves =
            leaveRepository.findUserLeaves(
                preMonthStartDate,
                preMonthEndDate,
                LeaveType.LOP.name,
                user.id
            )
        return userLeaves.flatMap { it.leaveDates }.count {
            (it.date == preMonthStartDate || it.date.after(preMonthStartDate))
                    && (it.date == preMonthEndDate || it.date.before(preMonthEndDate))
        }.toBigDecimal()
    }
}