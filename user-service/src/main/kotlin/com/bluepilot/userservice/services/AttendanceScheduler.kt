package com.bluepilot.userservice.services

import com.bluepilot.entities.Holiday
import com.bluepilot.entities.User
import com.bluepilot.enums.AttendanceStatus
import com.bluepilot.enums.UserStatus
import com.bluepilot.repositories.HolidaysRepository
import com.bluepilot.repositories.LeaveRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.userservice.mappers.UserSpecification
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.sql.Date
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime


@Service
class AttendanceScheduler @Autowired constructor(
    val attendanceService: AttendanceService,
    val holidaysRepository: HolidaysRepository,
    val leaveRepository: LeaveRepository,
    val userRepository: UserRepository
) {
    val logger: Logger = LoggerFactory.getLogger(AttendanceScheduler::class.java)

    @Scheduled(cron = "0 0 1 * * ?")
    fun schedule() {
        if (isSaturday()) updateAttendanceForUsersOnSaturdayOff()
        else if (isSunday()) updateAttendanceForUsersOnSunday()
        else if (getHolidaysList()?.firstOrNull { it.date == Date.valueOf(LocalDate.now()) } != null) {
            updateAttendanceForUsersWhenHoliday()
            logger.info("Updated Attendance for a holiday")
        } else
            updateAttendanceForUsersOnLeave()
    }

    fun isSunday(): Boolean = LocalDate.now().dayOfWeek == DayOfWeek.SUNDAY

    fun isSaturday(): Boolean = LocalDate.now().dayOfWeek == DayOfWeek.SATURDAY

    fun getUsersForAttendance(): List<User> {
        val specification = UserSpecification.withFilter(listOf(UserStatus.ACTIVE, UserStatus.ONBOARDED))
        return userRepository.findAll(specification, Pageable.unpaged()).content
    }

    //Updated on each day
    fun updateAttendanceForUsersOnLeave() {
        val date = Date.valueOf(LocalDate.now())
        val usersOnLeave = leaveRepository
            .findLeavesByDateRange(fromDate = date, toDate = date, pageable = Pageable.unpaged()).map { it.user }
        usersOnLeave.forEach { user -> attendanceService.saveAttendanceDetails(user, AttendanceStatus.LEAVE, null) }
        logger.info("Updated Attendance for ${usersOnLeave.size} employees on leave")
    }

    //Updated on days of holidays
    fun updateAttendanceForUsersWhenHoliday() = getUsersForAttendance()
        .forEach { attendanceService.saveAttendanceDetails(it, AttendanceStatus.HOLIDAY, null) }

    //Updated on every saturday
    fun updateAttendanceForUsersOnSaturdayOff() {
        getUsersForAttendance().forEach {
            if (it.userDetails!!.saturdayOff)
                attendanceService.saveAttendanceDetails(it, AttendanceStatus.WEEKEND, null)
        }
        logger.info("Updated attendance for Saturday")
    }

    //Updated on every sunday

    fun updateAttendanceForUsersOnSunday() {
        getUsersForAttendance().forEach {
            attendanceService.saveAttendanceDetails(it, AttendanceStatus.WEEKEND, null)
        }
        logger.info("Updated attendance for Sunday")
    }

    fun getHolidaysList(): List<Holiday>? {
        val holidaysList = holidaysRepository.findByYear(LocalDateTime.now().year)
        return holidaysList?.holidays?.firstOrNull { it.month == LocalDate.now().month.name }?.holidays
    }
}