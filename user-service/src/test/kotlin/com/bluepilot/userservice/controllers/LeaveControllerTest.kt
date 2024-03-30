package com.bluepilot.userservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.entities.LeaveDate
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.Role
import com.bluepilot.models.requests.LeaveRequest
import com.bluepilot.models.requests.LeavesFilter
import com.bluepilot.models.responses.LeaveDetailsResponse
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.LeaveRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.sql.Date
import java.time.Duration
import java.time.Instant

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class LeaveControllerTest @Autowired constructor(
    val authRoleRepository: AuthRoleRepository,
    val userRepository: UserRepository,
    val userGenerator: UserGenerator,
    val leaveRepository: LeaveRepository
) : BaseTestConfig() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var eventService: EventService

    @Test
    fun shouldApplyLeaveTest() {
        Mockito.`when`(eventService.sendEvent(null, NotificationEventType.LEAVE_APPLIED_APPLICATION, emptyMap()))
            .then {  }
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val savedUser = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        val token = "Bearer ${JwtService.generateToken(savedUser.authUser)}"

        val instant = Instant.now()
        val daysToAdd = 7L
        val date = instant.plus(Duration.ofDays(daysToAdd))
        val dates = mutableListOf(LeaveDate(Date(date.toEpochMilli())))
        for(i in 1..4){
            dates.add(LeaveDate(Date(instant.plus(Duration.ofDays(daysToAdd + i)).toEpochMilli())))
        }

        val leaveRequest = LeaveRequest(
            leaveType = LeaveType.PRIVILEGE_LEAVE,
            reason = "some other reason",
            leaveDates = dates.toList()
        )
        mockMvc.post("/employee/leave/apply") {
            contentType = MediaType.APPLICATION_JSON
            content =
                ObjectMapper().writeValueAsString(leaveRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
        val leaves = leaveRepository.findByUserId(savedUser.id)
        Assertions.assertTrue(leaveRequest.leaveDates.containsAll(leaves.first().leaveDates))
        Assertions.assertEquals(leaveRequest.leaveType, leaves.first().leaveType)
        Assertions.assertEquals(leaveRequest.reason, leaves.first().reason)
        Assertions.assertEquals(LeaveStatus.TO_BE_APPROVED, leaves.first().status)
    }

    @Test
    fun shouldNotApplyLeaveForInsufficientLeaveBalanceTest() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val savedUser = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        val token = "Bearer ${JwtService.generateToken(savedUser.authUser)}"

        val instant = Instant.now()
        val daysToAdd = 12L
        val date = instant.plus(Duration.ofDays(daysToAdd))
        val dates = mutableListOf(LeaveDate(Date(date.toEpochMilli())))
        for(i in 1..26){
            dates.add(LeaveDate(Date(instant.plus(Duration.ofDays(daysToAdd + i)).toEpochMilli())))
        }

        val leaveRequest = LeaveRequest(
            leaveType = LeaveType.PRIVILEGE_LEAVE,
            reason = "some other reason",
            leaveDates = dates
        )
        mockMvc.post("/employee/leave/apply") {
            contentType = MediaType.APPLICATION_JSON
            content =
                ObjectMapper().writeValueAsString(leaveRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotAcceptable() }
        }
    }

    @Test
    fun shouldNotApplyCompOffLeaveForInsufficientLeaveBalanceTest() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val savedUser = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        val token = "Bearer ${JwtService.generateToken(savedUser.authUser)}"

        val instant = Instant.now()
        val daysToAdd = 12L
        val date = instant.plus(Duration.ofDays(daysToAdd))
        val dates = mutableListOf(LeaveDate(Date(date.toEpochMilli())))
        for(i in 1..26){
            dates.add(LeaveDate(Date(instant.plus(Duration.ofDays(daysToAdd + i)).toEpochMilli())))
        }

        val leaveRequest = LeaveRequest(
            leaveType = LeaveType.COMPENSATORY_OFF,
            reason = "some other reason",
            leaveDates = dates
        )
        mockMvc.post("/employee/leave/apply") {
            contentType = MediaType.APPLICATION_JSON
            content =
                ObjectMapper().writeValueAsString(leaveRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotAcceptable() }
        }
    }

    @Test
    fun shouldNotApplyLOPLeaveIFLeaveBalanceExist() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val savedUser = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        val token = "Bearer ${JwtService.generateToken(savedUser.authUser)}"

        val instant = Instant.now()
        val daysToAdd = 12L
        val date = instant.plus(Duration.ofDays(daysToAdd))
        val dates = mutableListOf(LeaveDate(Date(date.toEpochMilli())))
        for(i in 1..16){
            dates.add(LeaveDate(Date(instant.plus(Duration.ofDays(daysToAdd + i)).toEpochMilli())))
        }

        val leaveRequest = LeaveRequest(
            leaveType = LeaveType.LOP,
            reason = "some other reason",
            leaveDates = dates
        )
        mockMvc.post("/employee/leave/apply") {
            contentType = MediaType.APPLICATION_JSON
            content =
                ObjectMapper().writeValueAsString(leaveRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotAcceptable() }
        }
    }


    @Test
    fun shouldApplySickLeaveInAdvanceTest() {
        Mockito.`when`(eventService.sendEvent(null, NotificationEventType.LEAVE_APPLIED_APPLICATION, emptyMap()))
            .then {  }

        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val savedUser = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        val token = "Bearer ${JwtService.generateToken(savedUser.authUser)}"

        val leaveRequest = LeaveRequest(
            leaveType = LeaveType.SICK_LEAVE,
            reason = "some other reason",
            leaveDates = listOf(LeaveDate(Date(Instant.now().toEpochMilli())))
        )
        mockMvc.post("/employee/leave/apply") {
            contentType = MediaType.APPLICATION_JSON
            content =
                ObjectMapper().writeValueAsString(leaveRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun shouldGetLeaveSummaryForLoggedInUserTest() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val savedUser = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        val token = "Bearer ${JwtService.generateToken(savedUser.authUser)}"

        val leaveRequest = LeaveRequest(
            leaveType = LeaveType.SICK_LEAVE,
            reason = "some other reason",
            leaveDates = listOf(LeaveDate(Date(Instant.now().toEpochMilli())))
        )
        mockMvc.post("/employee/leave/apply") {
            contentType = MediaType.APPLICATION_JSON
            content =
                ObjectMapper().writeValueAsString(leaveRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }

        val result = mockMvc.post("/employee/leave/summary") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                LeavesFilter(
                    status = LeaveStatus.TO_BE_APPROVED,
                )
            )
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            LeaveDetailsResponse::class.java
        )!!

        val leaveDetails = savedUser.userDetails!!.leaveDetails
        Assertions.assertEquals(savedUser.firstName + " " + savedUser.lastName, response.name)
        Assertions.assertEquals(leaveDetails.totalLeaves.setScale(1), response.totalLeaves)
        Assertions.assertEquals(leaveDetails.pendingLeaves.setScale(1), response.totalPendingLeaves)
        Assertions.assertEquals(leaveDetails.pendingPrivilegeLeave.setScale(1), response.totalPendingPrivilegeLeaves)
        Assertions.assertEquals(leaveDetails.pendingSickLeave.setScale(1), response.totalPendingSickLeaves)
        Assertions.assertEquals(leaveDetails.totalCompOffLeave.setScale(1), response.totalPendingCompOffLeaves)
        Assertions.assertEquals(1, response.leaveApplied.size)
        Assertions.assertEquals(LeaveStatus.TO_BE_APPROVED, response.leaveApplied.first().status)
        Assertions.assertEquals(LeaveStatus.TO_BE_APPROVED, response.leaveApplied.first().approvers.first().status)
    }
}