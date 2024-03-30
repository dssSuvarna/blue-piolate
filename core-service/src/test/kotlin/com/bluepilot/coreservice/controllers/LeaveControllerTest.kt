package com.bluepilot.coreservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.models.requests.LeavesApprovalFilter
import com.bluepilot.coreservice.models.responses.LeavesApprovalResponse
import com.bluepilot.entities.Leave
import com.bluepilot.entities.LeaveApprover
import com.bluepilot.entities.LeaveDate
import com.bluepilot.enums.Day
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.bluepilot.enums.Role
import com.bluepilot.models.responses.LeaveDetailsResponse
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.models.responses.UpcomingLeaveResponse
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.AuthUserRepository
import com.bluepilot.repositories.LeaveRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
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
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.math.BigDecimal
import java.sql.Date
import java.time.Duration
import java.time.Instant

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class LeaveControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val userGenerator: UserGenerator,
    val authUserRepository: AuthUserRepository,
    val authRoleRepository: AuthRoleRepository,
    val leaveRepository: LeaveRepository
) : BaseTestConfig() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var eventService: EventService

    @BeforeEach
    fun saveOnboardingContext() {
        Mockito.`when`(eventService.processEvent(com.bluepilot.utils.Mockito.anyObject())).then {  }
    }


    @Test
    fun approveLeaveByAdminTest() {
        val adminUser = authUserRepository.findById(1)
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user1 = userRepository.save(
            userGenerator.getUser(authRole = employeeRole!!)
        )

        val user = userGenerator.getUser(authRole = employeeRole, userName = "user2@gmail.com")
        user.also { it.reporter = user1 }
        val user2 = userRepository.save(
            user
        )

        val instant = Instant.now()
        val daysToAdd = 12L
        val leaveDate1 = instant.plus(Duration.ofDays(daysToAdd))
        val leaveDate2 = instant.plus(Duration.ofDays(daysToAdd + 2))
        val leaveDate3 = instant.plus(Duration.ofDays(daysToAdd + 3))
        val leaveDate4 = instant.plus(Duration.ofDays(daysToAdd + 4))


        val date1 = LeaveDate(Date(leaveDate1.toEpochMilli()), Day.HALF_DAY)
        val date2 = LeaveDate(Date(leaveDate2.toEpochMilli()))
        val date3 = LeaveDate(Date(leaveDate3.toEpochMilli()))
        val date4 = LeaveDate(Date(leaveDate4.toEpochMilli()))

        val leaveType = LeaveType.PRIVILEGE_LEAVE
        val reason = "some other reason"

        val approvers = mutableListOf(
            LeaveApprover(user = user2.reporter!!, status = LeaveStatus.TO_BE_APPROVED),
            LeaveApprover(
                user = userRepository.findUserByAuthUser("hr@gmail.com")!!,
                status = LeaveStatus.TO_BE_APPROVED
            ),
            LeaveApprover(
                user = userRepository.findUserByAuthUser("admin@gmail.com")!!,
                status = LeaveStatus.TO_BE_APPROVED
            )
        )

        val savedLeave = leaveRepository.save(
            Leave(
                user = user2,
                leaveDates = listOf(date1, date2, date3, date4),
                status = LeaveStatus.TO_BE_APPROVED,
                leaveType = leaveType,
                approvalFrom = approvers,
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = reason
            )
        )
        val token = "Bearer ${JwtService.generateToken(adminUser.get())}"

        mockMvc.put("/admin/leave/approve") {
            contentType = MediaType.APPLICATION_JSON
            param("status", "${LeaveStatus.APPROVED}")
            param("leaveId", "${savedLeave.id}")
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        val approvedSavedLeave = leaveRepository.findById(savedLeave.id).get()
        assertEquals(LeaveStatus.APPROVED, approvedSavedLeave.status)
        assertEquals(BigDecimal.valueOf(3.5), approvedSavedLeave.getTotalLeaveDays())
    }

    @Test
    fun shouldApproveLeaveTest() {
        val hrUser = authUserRepository.findById(2)
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user1 = userRepository.save(
            userGenerator.getUser(authRole = employeeRole!!, userName = "user4@gmail.com")
        )

        val user = userGenerator.getUser(authRole = employeeRole, userName = "user5@gmail.com")
        user.also { it.reporter = user1 }
        val user2 = userRepository.save(
            user
        )

        val instant = Instant.now()
        val daysToAdd = 12L
        val leaveDates = mutableListOf<LeaveDate>()
        for (i in 1..5) {
            val leaveDate = instant.plus(Duration.ofDays(daysToAdd + i))
            leaveDates.add(LeaveDate(Date(leaveDate.toEpochMilli())))
        }
        val leaveType = LeaveType.PRIVILEGE_LEAVE
        val reason = "some other reason"

        val approvers = mutableListOf(
            LeaveApprover(user = user2.reporter!!, status = LeaveStatus.TO_BE_APPROVED),
            LeaveApprover(
                user = userRepository.findUserByAuthUser("hr@gmail.com")!!,
                status = LeaveStatus.TO_BE_APPROVED
            ),
            LeaveApprover(
                user = userRepository.findUserByAuthUser("admin@gmail.com")!!,
                status = LeaveStatus.TO_BE_APPROVED
            )
        )

        val savedLeave = leaveRepository.save(
            Leave(
                user = user2,
                status = LeaveStatus.TO_BE_APPROVED,
                leaveDates = leaveDates,
                leaveType = leaveType,
                approvalFrom = approvers,
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = reason
            )
        )
        val userToken = "Bearer ${JwtService.generateToken(user1.authUser)}"

        mockMvc.put("/admin/leave/approve") {
            contentType = MediaType.APPLICATION_JSON
            param("status", "${LeaveStatus.APPROVED}")
            param("leaveId", "${savedLeave.id}")
            headers { header(name = "Authorization", userToken) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        //When only one approval
        var approvedSavedLeave = leaveRepository.findById(savedLeave.id).get()
        assertEquals(LeaveStatus.TO_BE_APPROVED, approvedSavedLeave.status)

        val hrToken = "Bearer ${JwtService.generateToken(hrUser.get())}"

        mockMvc.put("/admin/leave/approve") {
            contentType = MediaType.APPLICATION_JSON
            param("status", "${LeaveStatus.APPROVED}")
            param("leaveId", "${savedLeave.id}")
            headers { header(name = "Authorization", hrToken) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        //When only more than or equal two approvals
        approvedSavedLeave = leaveRepository.findById(savedLeave.id).get()
        assertEquals(LeaveStatus.APPROVED, approvedSavedLeave.status)
    }

    @Test
    fun shouldRejectLeaveTest() {
        val hrUser = authUserRepository.findById(2)
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user1 = userRepository.save(
            userGenerator.getUser(authRole = employeeRole!!, userName = "user6@gmail.com")
        )

        val user = userGenerator.getUser(authRole = employeeRole, userName = "user7@gmail.com")
        user.also { it.reporter = user1 }
        val user2 = userRepository.save(
            user
        )

        val instant = Instant.now()
        val daysToAdd = 12L

        val leaveDates = mutableListOf<LeaveDate>()

        for (i in 1..15) {
            val leaveDate = instant.plus(Duration.ofDays(daysToAdd + 1))
            leaveDates.add(LeaveDate(Date(leaveDate.toEpochMilli())))
        }

        val leaveType = LeaveType.PRIVILEGE_LEAVE
        val reason = "some other reason"

        val approvers = mutableListOf(
            LeaveApprover(user = user2.reporter!!, status = LeaveStatus.TO_BE_APPROVED),
            LeaveApprover(
                user = userRepository.findUserByAuthUser("hr@gmail.com")!!,
                status = LeaveStatus.TO_BE_APPROVED
            ),
            LeaveApprover(
                user = userRepository.findUserByAuthUser("admin@gmail.com")!!,
                status = LeaveStatus.TO_BE_APPROVED
            )
        )

        val savedLeave = leaveRepository.save(
            Leave(
                user = user2,
                status = LeaveStatus.TO_BE_APPROVED,
                leaveType = leaveType,
                leaveDates = leaveDates,
                approvalFrom = approvers,
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = reason
            )
        )

        val hrToken = "Bearer ${JwtService.generateToken(hrUser.get())}"

        mockMvc.put("/admin/leave/approve") {
            contentType = MediaType.APPLICATION_JSON
            param("status", "${LeaveStatus.REJECTED}")
            param("leaveId", "${savedLeave.id}")
            headers { header(name = "Authorization", hrToken) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        val approvedSavedLeave = leaveRepository.findById(savedLeave.id).get()
        assertEquals(LeaveStatus.REJECTED, approvedSavedLeave.status)
    }

    @Test
    fun shouldGetPendingLeavesToBeApproved() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user1 = userRepository.save(
            userGenerator.getUser(authRole = employeeRole!!)
        )

        val user = userGenerator.getUser(authRole = employeeRole, userName = "user@gmail.com")
        user.also { it.reporter = user1 }
        val user2 = userRepository.save(
            user
        )

        val instant = Instant.now()
        val daysToAdd = 12L

        val leaveDates = mutableListOf<LeaveDate>()
        for (i in 1..15) {
            val leaveDate = instant.plus(Duration.ofDays(daysToAdd + 1))
            leaveDates.add(LeaveDate(Date(leaveDate.toEpochMilli())))
        }
        val leaveType = LeaveType.PRIVILEGE_LEAVE
        val reason = "some other reason"

        val approvers = mutableListOf(
            LeaveApprover(user = user2.reporter!!, status = LeaveStatus.TO_BE_APPROVED),
            LeaveApprover(
                user = userRepository.findUserByAuthUser("hr@gmail.com")!!,
                status = LeaveStatus.TO_BE_APPROVED
            ),
            LeaveApprover(
                user = userRepository.findUserByAuthUser("admin@gmail.com")!!,
                status = LeaveStatus.TO_BE_APPROVED
            )
        )

        val savedLeave = leaveRepository.save(
            Leave(
                user = user2,
                status = LeaveStatus.TO_BE_APPROVED,
                leaveType = leaveType,
                leaveDates = leaveDates,
                approvalFrom = approvers,
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = reason
            )
        )

        val authUser1 = authUserRepository.findById(user1.id)
        val token = "Bearer ${JwtService.generateToken(authUser1.get())}"
        val result = mockMvc.post("/admin/leave/approvals") {
            param("pageNumber", 0.toString())
            param("pageSize", 10.toString())
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                LeavesApprovalFilter(
                    status = LeaveStatus.TO_BE_APPROVED
                )
            )
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString, object : TypeReference<PageResponse<LeavesApprovalResponse>>() {}
        )!!

        for (i in response.contents.indices) {
            assertEquals(savedLeave.id, response.contents[i].leaveId)
            assertEquals(savedLeave.user.firstName, response.contents[i].firstName)
            assertEquals(savedLeave.reason, response.contents[i].reason)
            /* for (j in response.contents[i].approvers.indices) {
                 assertEquals(savedLeave.approvalFrom[j].user.designation, response.contents[i].approvers[j].designation)
                 assertEquals(
                     savedLeave.approvalFrom[j].user.employeeCode, response.contents[i].approvers[j].employeeCode)
                 assertEquals(
                     savedLeave.approvalFrom[j].user.profilePicture, response.contents[i].approvers[j].profilePicture)
             }*/
        }
    }

    @Test
    fun shouldGetLeavesForApproverWithFilter() {
        /**
         * Filtering based on leave status = "TO BE APPROVED"
         */
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)
        //Reporter - ADMIN
        val user1 = userRepository.save(
            userGenerator.getUser(authRole = employeeRole!!)
        )

        val user = userGenerator.getUser(authRole = employeeRole, userName = "user@gmail.com")
        //Reporter - User1
        user.also { it.reporter = user1 }
        val user2 = userRepository.save(
            user
        )
        val instant = Instant.now()
        val daysToAdd = 12L

        val leaveDates = mutableListOf<LeaveDate>()

        for (i in 1..4) {
            val leaveDate = instant.plus(Duration.ofDays(daysToAdd + 1))
            leaveDates.add(LeaveDate(Date(leaveDate.toEpochMilli())))
        }
        val userApprover = LeaveApprover(user = user2.reporter!!, status = LeaveStatus.TO_BE_APPROVED)
        val hrApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("hr@gmail.com")!!,
            status = LeaveStatus.TO_BE_APPROVED
        )
        val adminApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("admin@gmail.com")!!,
            status = LeaveStatus.TO_BE_APPROVED
        )

        val leavesList = listOf(
            /**
             * Reporter applying leave
             * Approvers would be his reporter, HR and Admin
             * In this case he is reporting to admin
             */
            Leave(
                user = user1,
                status = LeaveStatus.TO_BE_APPROVED,
                leaveType = LeaveType.PRIVILEGE_LEAVE,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            ),
            /**
             * Reportee applying leave
             * Approvers would be his reporter, HR and Admin
             */
            Leave(
                user = user2,
                status = LeaveStatus.TO_BE_APPROVED,
                leaveType = LeaveType.PRIVILEGE_LEAVE,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(userApprover, hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            )
        )
        val savedLeaves = leaveRepository.saveAll(leavesList).toList()

        /**
         * Reporter can only fetch his reportee's leave information to approve
         */
        val authUser1 = authUserRepository.findById(user1.id)
        val token1 = "Bearer ${JwtService.generateToken(authUser1.get())}"
        val result1 = mockMvc.post("/admin/leave/approvals") {
            param("pageNumber", 0.toString())
            param("pageSize", 10.toString())
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                LeavesApprovalFilter(
                    status = LeaveStatus.TO_BE_APPROVED
                )
            )
            headers { header(name = "Authorization", token1) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val response1 = ObjectMapper().readValue(
            result1.response.contentAsString, object : TypeReference<PageResponse<LeavesApprovalResponse>>() {}
        )!!

        assertEquals(1, response1.contents.size)

        /**
         * Admin/HR can fetch employees leave requests irrespective of their reporter
         */

        val adminUser = authUserRepository.findById(1)
        val token2 = "Bearer ${JwtService.generateToken(adminUser.get())}"
        val result2 = mockMvc.post("/admin/leave/approvals") {
            param("pageNumber", 0.toString())
            param("pageSize", 10.toString())
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                LeavesApprovalFilter(
                    status = LeaveStatus.TO_BE_APPROVED
                )
            )
            headers { header(name = "Authorization", token2) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val response2 = ObjectMapper().readValue(
            result2.response.contentAsString, object : TypeReference<PageResponse<LeavesApprovalResponse>>() {}
        )!!

        assertEquals(savedLeaves.size, response2.contents.size)

        /**
         * Filtering based on leave status = "REJECTED"
         */
        val result3 = mockMvc.post("/admin/leave/approvals") {
            param("pageNumber", 0.toString())
            param("pageSize", 10.toString())
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                LeavesApprovalFilter(
                    status = LeaveStatus.REJECTED
                )
            )
            headers { header(name = "Authorization", token2) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val response3 = ObjectMapper().readValue(
            result3.response.contentAsString, object : TypeReference<PageResponse<LeavesApprovalResponse>>() {}
        )!!

        /**
         * No Leaves are present with status = "REJECTED"
         */
        assertEquals(0, response3.contents.size)
    }

    @Test
    fun getLeaveSummaryOfAllEmployeeTest() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user1 = userRepository.save(
            userGenerator.getUser(authRole = employeeRole!!)
        )

        val user = userGenerator.getUser(authRole = employeeRole, userName = "user@gmail.com")
        user.also { it.reporter = user1 }
        val user2 = userRepository.save(
            user
        )
        val instant = Instant.now()
        val daysToAdd = 12L

        val leaveDates = mutableListOf<LeaveDate>()
        for (i in 1..4) {
            val leaveDate = instant.plus(Duration.ofDays(daysToAdd + i))
            leaveDates.add(LeaveDate(Date(leaveDate.toEpochMilli())))
        }


        val userApprover = LeaveApprover(user = user2.reporter!!, status = LeaveStatus.TO_BE_APPROVED)
        val hrApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("hr@gmail.com")!!,
            status = LeaveStatus.TO_BE_APPROVED
        )
        val adminApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("admin@gmail.com")!!,
            status = LeaveStatus.TO_BE_APPROVED
        )

        val leavesList = listOf(
            Leave(
                user = user1,
                status = LeaveStatus.TO_BE_APPROVED,
                leaveType = LeaveType.PRIVILEGE_LEAVE,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            ),
            Leave(
                user = user2,
                status = LeaveStatus.TO_BE_APPROVED,
                leaveType = LeaveType.PRIVILEGE_LEAVE,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(userApprover, hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            )
        )
        leaveRepository.saveAll(leavesList).toList()

        val adminUser = authUserRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get())}"

        val mvcResult = mockMvc.get("/admin/leave/summary") {
            param("pageNumber", "0")
            param("pageSize", "10")
            headers { header(name = "Authorization", token) }
            contentType = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }
            .andReturn()

        val responseBody = mvcResult.response.contentAsString
        val response = ObjectMapper().readValue(
            responseBody,
            object : TypeReference<PageResponse<LeaveDetailsResponse>>() {}
        )

        assertEquals(2, response.currentPageSize)
        assertEquals(0, response.pageNumber)
        assertEquals(2, response.totalCount)
        response.contents.forEach {
            assertEquals(BigDecimal(20.0).setScale(1), it.totalLeaves.setScale(1))
            assertEquals(LeaveStatus.TO_BE_APPROVED, it.leaveApplied.first().status)
            assertEquals("reason", it.leaveApplied.first().reason)
        }
    }

    @Test
    fun getUpcomingLeaveOfEmployeesTest() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user = userGenerator.getUser(authRole = employeeRole!!, userName = "user@gmail.com")
        val user1 = userRepository.save(
            userGenerator.getUser(authRole = employeeRole)
        )

        user.also { it.reporter = user1 }
        val user2 = userRepository.save(
            user
        )
        val instant = Instant.now()
        val daysToAdd = 12L

        val leaveDates = mutableListOf<LeaveDate>()
        for (i in 1..4) {
            val leaveDate = instant.plus(Duration.ofDays(daysToAdd + i))
            leaveDates.add(LeaveDate(Date(leaveDate.toEpochMilli())))
        }
        val userApprover = LeaveApprover(user = user2.reporter!!, status = LeaveStatus.APPROVED)
        val hrApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("hr@gmail.com")!!,
            status = LeaveStatus.APPROVED
        )
        val adminApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("admin@gmail.com")!!,
            status = LeaveStatus.TO_BE_APPROVED
        )

        val leavesList = listOf(
            Leave(
                user = user1,
                status = LeaveStatus.APPROVED,
                leaveType = LeaveType.PRIVILEGE_LEAVE,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            ),
            Leave(
                user = user2,
                status = LeaveStatus.APPROVED,
                leaveType = LeaveType.PRIVILEGE_LEAVE,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(userApprover, hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            )
        )
        leaveRepository.saveAll(leavesList).toList()
        val adminUser = authUserRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get())}"


        val mvcResult = mockMvc.get("/admin/leave/upcoming-leaves") {
            param("pageNumber", "0")
            param("pageSize", "10")
            param("dateRange", "20")
            headers { header(name = "Authorization", token) }
            contentType = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }
            .andReturn()

        val responseBody = mvcResult.response.contentAsString
        val response = ObjectMapper().readValue(
            responseBody,
            object : TypeReference<PageResponse<UpcomingLeaveResponse>>() {}
        )

        assertEquals(2, response.currentPageSize)
        assertEquals(0, response.pageNumber)
        assertEquals(2, response.totalCount)
        //user1
        assertEquals(user1.firstName + " " + user1.lastName, response.contents.first().name)
        assertEquals(user1.designation, response.contents.first().designation)
        assertEquals(user1.userDetails!!.professionalEmail, response.contents.first().professionalEmail)
        assertEquals(user1.designation, response.contents.first().designation)

        //user2
        assertEquals(user2.firstName + " " + user1.lastName, response.contents.last().name)
        assertEquals(user2.designation, response.contents.last().designation)
        assertEquals(user2.userDetails!!.professionalEmail, response.contents.last().professionalEmail)
    }
}