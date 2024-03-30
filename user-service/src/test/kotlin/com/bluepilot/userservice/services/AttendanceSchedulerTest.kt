package com.bluepilot.userservice.services

import com.bluepilot.entities.Holiday
import com.bluepilot.entities.HolidayList
import com.bluepilot.entities.LeaveDate
import com.bluepilot.entities.MonthHolidays
import com.bluepilot.enums.AttendanceStatus
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.bluepilot.enums.Role
import com.bluepilot.enums.UserStatus
import com.bluepilot.models.requests.LeaveRequest
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AttendanceRepository
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.HolidaysRepository
import com.bluepilot.repositories.LeaveRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Date
import java.time.LocalDate

@SpringBootTest
@RunWith(SpringRunner::class)
class AttendanceSchedulerTest @Autowired constructor(
    private val attendanceScheduler: AttendanceScheduler,
    private val attendanceRepository: AttendanceRepository,
    private val authRoleRepository: AuthRoleRepository,
    private val leaveService: LeaveService,
    private val leaveRepository: LeaveRepository,
    private val userGenerator: UserGenerator,
    private val userRepository: UserRepository,
    private val holidaysRepository: HolidaysRepository
) : BaseTestConfig() {

    @MockBean
    lateinit var eventService: EventService

    @Test
    fun shouldUpdateAttendanceOfAllUsersWhenHoliday() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole, status = UserStatus.ACTIVE))
        val today = Date.valueOf(LocalDate.now())

        holidaysRepository.save(
            HolidayList(
                year = LocalDate.now().year, holidays = listOf(
                    MonthHolidays(
                        month = LocalDate.now().month.name,
                        holidays = listOf(Holiday(name = "Holiday", date = today, description = "Holiday"))
                    )
                )
            )
        )

        val attendanceSchedulerMock = Mockito.spy(attendanceScheduler)
        `when`(attendanceSchedulerMock.isSunday()).thenReturn(false)
        `when`(attendanceSchedulerMock.isSaturday()).thenReturn(false)
        attendanceSchedulerMock.schedule()
        verify(attendanceSchedulerMock, atLeastOnce()).updateAttendanceForUsersWhenHoliday()
        val attendanceDetails = attendanceRepository.findByDateAndUserId(today, user.id)
        Assertions.assertNotNull(attendanceDetails)
        Assertions.assertEquals(attendanceDetails!!.status, AttendanceStatus.HOLIDAY)
    }

    @Test
    fun shouldUpdateAttendanceOnSunday() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole, status = UserStatus.ACTIVE))
        val today = Date.valueOf(LocalDate.now())

        val attendanceSchedulerMock = Mockito.spy(attendanceScheduler)
        `when`(attendanceSchedulerMock.isSunday()).thenReturn(true)
        `when`(attendanceSchedulerMock.isSaturday()).thenReturn(false)
        attendanceSchedulerMock.schedule()
        verify(attendanceSchedulerMock, atLeastOnce()).updateAttendanceForUsersOnSunday()

        val attendanceDetails = attendanceRepository.findByDateAndUserId(today, user.id)
        Assertions.assertNotNull(attendanceDetails)
        Assertions.assertEquals(attendanceDetails!!.status, AttendanceStatus.WEEKEND)
    }

    @Test
    fun shouldUpdateAttendanceForUsersOnLeave() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole, status = UserStatus.ACTIVE))
        val today = Date.valueOf(LocalDate.now())

        `when`(eventService.processEvent(com.bluepilot.utils.Mockito.anyObject())).then { }

        val leaveRequest = LeaveRequest(
            leaveType = LeaveType.PRIVILEGE_LEAVE,
            reason = "some other reason",
            leaveDates = listOf(LeaveDate(date = today))
        )
        leaveService.applyLeave(leaveRequest, user)
        val leave = leaveRepository.findByUserId(user.id).first()
        leave.status = LeaveStatus.APPROVED
        leaveRepository.save(leave)

        val attendanceSchedulerMock = Mockito.spy(attendanceScheduler)
        `when`(attendanceSchedulerMock.isSunday()).thenReturn(false)
        `when`(attendanceSchedulerMock.isSaturday()).thenReturn(false)
        attendanceSchedulerMock.schedule()
        verify(attendanceSchedulerMock, atLeastOnce()).updateAttendanceForUsersOnLeave()

        val attendanceDetails = attendanceRepository.findByDateAndUserId(today, user.id)
        Assertions.assertNotNull(attendanceDetails)
        Assertions.assertEquals(attendanceDetails!!.status, AttendanceStatus.LEAVE)
    }
}