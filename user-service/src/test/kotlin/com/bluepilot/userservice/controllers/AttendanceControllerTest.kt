package com.bluepilot.userservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.entities.Attendance
import com.bluepilot.entities.BreakLogs
import com.bluepilot.enums.AttendanceEvent
import com.bluepilot.enums.AttendanceStatus
import com.bluepilot.enums.Role
import com.bluepilot.models.responses.Response
import com.bluepilot.repositories.AttendanceRepository
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import com.bluepilot.userservice.services.AttendanceService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.sql.Date
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class AttendanceControllerTest @Autowired constructor(
    private val attendanceService: AttendanceService,
    private val attendanceRepository: AttendanceRepository,
    private val authRoleRepository: AuthRoleRepository,
    private val userGenerator: UserGenerator,
    private val userRepository: UserRepository
): BaseTestConfig() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun userLogin() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"
        val event = AttendanceEvent.LOGIN

        val result = mockMvc.post("/employee/attendance") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(event)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper()
            .readValue(result.response.contentAsString, Response::class.java)
        Assertions.assertEquals(response.response, "Logged in")

        val loginDetails = attendanceRepository.findByDateAndUserId(Date.valueOf(LocalDate.now()), user.id)
        Assertions.assertNotNull(loginDetails!!.login)
    }

    @Test
    fun userLogout() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

        attendanceRepository.save(
            Attendance (
                date = Date(Instant.now().toEpochMilli()),
                userId = user.id,
                login = Timestamp.from(Instant.now().minus(4, ChronoUnit.HOURS)),
                status = AttendanceStatus.PRESENT,
            )
        )

        val event = AttendanceEvent.LOGOUT
        mockMvc.post("/employee/attendance") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(event)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()


        val logoutDetails = attendanceRepository.findByDateAndUserId(Date.valueOf(LocalDate.now()), user.id)
        Assertions.assertNotNull(logoutDetails!!.logout)

        val loginHours = attendanceService.getTimeInSeconds(logoutDetails.loginHours)
        val loginLogoutDifference = Duration.between(
            logoutDetails.login!!.toLocalDateTime(), logoutDetails.logout!!.toLocalDateTime()
        ).seconds

        Assertions.assertEquals(loginHours.toLong(), loginLogoutDifference)
    }

    @Test
    fun startBreak() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

      attendanceRepository.save(
            Attendance (
                date = Date(Instant.now().toEpochMilli()),
                userId = user.id,
                login = Timestamp.from(Instant.now().minus(4, ChronoUnit.HOURS)),
                status = AttendanceStatus.PRESENT,
            )
        )

        val event = AttendanceEvent.BREAK_START
        mockMvc.post("/employee/attendance") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(event)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val breakDetails = attendanceRepository.findByDateAndUserId(Date.valueOf(LocalDate.now()), user.id)!!.logs
        Assertions.assertNotNull(breakDetails!!.first().checkIn)
    }

    @Test
    fun endBreak() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"
        val breakStart = Timestamp.from(Instant.now().minus(4, ChronoUnit.HOURS))

        attendanceRepository.save(
            Attendance (
                date = Date(Instant.now().toEpochMilli()),
                userId = user.id,
                login = Timestamp.from(Instant.now().minus(4, ChronoUnit.HOURS)),
                status = AttendanceStatus.PRESENT,
                logs = mutableListOf(BreakLogs(checkIn = breakStart))
            )
        )

        val event = AttendanceEvent.BREAK_END
        mockMvc.post("/employee/attendance") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(event)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val attendanceDetails = attendanceRepository.findByDateAndUserId(Date.valueOf(LocalDate.now()), user.id)!!
        val breakDetails = attendanceDetails.logs!!.first()
        Assertions.assertNotNull(breakDetails.checkOut)

        val expectedBreakTime = Duration.between(
            breakDetails.checkIn.toLocalDateTime(), breakDetails.checkOut!!.toLocalDateTime()
        ).seconds
        val actualBreakTime = attendanceService.getTimeInSeconds(attendanceDetails.breakDuration)
        Assertions.assertEquals(expectedBreakTime, actualBreakTime.toLong())
    }
}