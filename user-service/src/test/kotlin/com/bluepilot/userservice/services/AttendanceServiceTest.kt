package com.bluepilot.userservice.services

import com.bluepilot.configs.JwtService
import com.bluepilot.entities.Attendance
import com.bluepilot.enums.AttendanceEvent
import com.bluepilot.enums.AttendanceStatus
import com.bluepilot.enums.Role
import com.bluepilot.errors.ErrorMessages
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.repositories.AttendanceRepository
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@RunWith(SpringRunner::class)
class AttendanceServiceTest @Autowired constructor(
    private val attendanceService: AttendanceService,
    private val attendanceRepository: AttendanceRepository,
    private val authRoleRepository: AuthRoleRepository,
    private val userGenerator: UserGenerator,
    private val userRepository: UserRepository
) : BaseTestConfig() {

    @Test
    fun userCannotLogoutMultipleTimes() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

        attendanceRepository.save(
            Attendance(
                date = Date(Instant.now().toEpochMilli()),
                userId = user.id,
                login = Timestamp.from(Instant.now().minus(4, ChronoUnit.HOURS)),
                status = AttendanceStatus.PRESENT,
                logout = Timestamp.from(Instant.now())
            )
        )

        val exception = Assertions.assertThrows(NotAllowedException::class.java) {
            attendanceService.attendanceEventHandler(token, AttendanceEvent.LOGOUT)
        }.error

        Assertions.assertEquals(exception.message, ErrorMessages.ALREADY_LOGGED_OUT)
    }
}