package com.bluepilot.userservice.services

import com.bluepilot.entities.LeaveDate
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.Role
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.models.requests.LeaveRequest
import com.bluepilot.models.requests.LeavesFilter
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.LeaveRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Date
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

@SpringBootTest
@RunWith(SpringRunner::class)
class LeaveServiceTest @Autowired constructor(
    val authRoleRepository: AuthRoleRepository,
    val userRepository: UserRepository,
    val userGenerator: UserGenerator,
    val leaveRepository: LeaveRepository,
    val leaveService: LeaveService
) : BaseTestConfig() {

    @MockBean
    lateinit var eventService: EventService

    @Test
    fun shouldNotApplyMultipleLeaveOnSameDayTest() {
        Mockito.`when`(eventService.sendEvent(null, NotificationEventType.LEAVE_APPLIED_APPLICATION, emptyMap()))
            .then { }
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val instant = Instant.now()
        val daysToAdd = 7L
        val date = instant.plus(Duration.ofDays(daysToAdd))
        val dates = mutableListOf(LeaveDate(Date(date.toEpochMilli())))
        for (i in 1..4) {
            dates.add(LeaveDate(Date(instant.plus(Duration.ofDays(daysToAdd + i)).toEpochMilli())))
        }

        val leaveRequest = LeaveRequest(
            leaveType = LeaveType.PRIVILEGE_LEAVE,
            reason = "some other reason",
            leaveDates = dates.toList()
        )
        leaveService.applyLeave(leaveRequest, user)
        val leave = leaveRepository.findByUserId(user.id).first()
        leave.status = LeaveStatus.APPROVED
        leaveRepository.save(leave)

        val leaveRequest1 = LeaveRequest(
            leaveType = LeaveType.PRIVILEGE_LEAVE,
            reason = "some other reason",
            leaveDates = listOf(LeaveDate(Date(instant.plus(Duration.ofDays(daysToAdd + 1)).toEpochMilli())))
        )
        Assertions.assertThrows(
            NotAllowedException::class.java,
            Executable { leaveService.applyLeave(leaveRequest1, user) }
        )
    }

    @Test
    fun leaveSummaryFilterTest() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        val leaveRequest = LeaveRequest(
            leaveType = LeaveType.SICK_LEAVE,
            reason = "some other reason",
            leaveDates = listOf(LeaveDate(Date(Instant.now().toEpochMilli())))
        )
        leaveService.applyLeave(leaveRequest, user)

        val leaveRequest1 = LeaveRequest(
            leaveType = LeaveType.PRIVILEGE_LEAVE,
            reason = "some other reason",
            leaveDates = listOf(LeaveDate(Date(Instant.now().plus(Duration.ofDays(1)).toEpochMilli())))
        )

        leaveService.applyLeave(leaveRequest1, user)
        var result = leaveService.getLeaveSummary(
            user, LeavesFilter(
                status = LeaveStatus.TO_BE_APPROVED,
                leaveType = LeaveType.SICK_LEAVE
            )
        )
        Assertions.assertEquals(1, result.leaveApplied.size)
        Assertions.assertEquals(LeaveStatus.TO_BE_APPROVED, result.leaveApplied.first().status)
        Assertions.assertEquals(LeaveType.SICK_LEAVE, result.leaveApplied.first().leaveType)

        result = leaveService.getLeaveSummary(
            user, LeavesFilter(
                status = LeaveStatus.TO_BE_APPROVED,
                leaveType = LeaveType.PRIVILEGE_LEAVE
            )
        )

        Assertions.assertEquals(1, result.leaveApplied.size)
        Assertions.assertEquals(LeaveStatus.TO_BE_APPROVED, result.leaveApplied.first().status)
        Assertions.assertEquals(LeaveType.PRIVILEGE_LEAVE, result.leaveApplied.first().leaveType)


        result = leaveService.getLeaveSummary(
            user, LeavesFilter(
                status = LeaveStatus.APPROVED,
                leaveType = LeaveType.PRIVILEGE_LEAVE,
                year = LocalDateTime.now().year
            )
        )
        Assertions.assertEquals(0, result.leaveApplied.size)
    }
}