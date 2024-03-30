package com.bluepilot.userservice.services

import com.bluepilot.entities.Attendance
import com.bluepilot.entities.BreakLogs
import com.bluepilot.entities.User
import com.bluepilot.enums.AttendanceEvent
import com.bluepilot.enums.AttendanceStatus
import com.bluepilot.errors.ErrorMessages.Companion.ALREADY_LOGGED_OUT
import com.bluepilot.errors.ErrorMessages.Companion.BREAK_ALREADY_STARTED
import com.bluepilot.errors.ErrorMessages.Companion.NO_ACTIVE_BREAK
import com.bluepilot.errors.NotAllowed
import com.bluepilot.errors.ResourceNotFound
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.exceptions.Validator.Companion.validate
import com.bluepilot.models.responses.Response
import com.bluepilot.repositories.AttendanceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

@Service
@Transactional
class AttendanceService @Autowired constructor(
    private val attendanceRepository: AttendanceRepository,
    private val userService: UserService
) {

    fun attendanceEventHandler(token: String, event: AttendanceEvent): Response {
        val user = userService.getUserFromToken(token)
        val currentDate = Date(Instant.now().toEpochMilli())
        val attendanceDetails = attendanceRepository.findByDateAndUserId(currentDate, user.id)
        when (event) {
            AttendanceEvent.LOGIN -> {
                if (attendanceDetails == null) saveAttendanceDetails(user)
                return Response("Logged in")
            }

            AttendanceEvent.LOGOUT -> {
                throwExceptionIfUserIsLoggedOut(attendanceDetails ?: throw NotFoundException(ResourceNotFound()))
                return logout(attendanceDetails)
            }

            AttendanceEvent.BREAK_START -> {
                throwExceptionIfUserIsLoggedOut(attendanceDetails ?: throw NotFoundException(ResourceNotFound()))
                return startBreak(attendanceDetails)
            }

            AttendanceEvent.BREAK_END -> {
                throwExceptionIfUserIsLoggedOut(attendanceDetails ?: throw NotFoundException(ResourceNotFound()))
                return endBreak(attendanceDetails)
            }
        }
    }

    fun saveAttendanceDetails(
        user: User,
        status: AttendanceStatus = AttendanceStatus.PRESENT,
        login: Timestamp? = Timestamp.from(Instant.now())
    ) {
        attendanceRepository.save(
            Attendance(
                date = Date(Instant.now().toEpochMilli()),
                userId = user.id,
                login = login,
                status = status,
            )
        )
    }

    fun logout(attendance: Attendance): Response {
        val breakTime = getTimeInSeconds(attendance.breakDuration)
        val logoutTime = LocalDateTime.now()
        attendance.apply {
            logout = Timestamp.from(Instant.now())
            loginHours = setTimeFromSeconds(
                Duration.between(
                    login!!.toLocalDateTime(), logoutTime
                ).seconds - breakTime
            )
        }
        return Response("Logged out")
    }

    fun startBreak(attendance: Attendance): Response {
        if (attendance.logs.isEmpty()) attendance.logs.add(BreakLogs(checkIn = Timestamp.from(Instant.now())))
        else {
            validate(
                attendance.logs.last().checkOut == null,
                NotAllowedException(NotAllowed(message = BREAK_ALREADY_STARTED))
            )
            attendance.logs.add(BreakLogs(checkIn = Timestamp.from(Instant.now())))
        }
        return Response("Break started")
    }

    fun endBreak(attendance: Attendance): Response {
        val currentBreak = attendance.logs.last().takeIf { it.checkOut == null || attendance.logs.isEmpty() }
            ?: throw NotAllowedException(NotAllowed(message = NO_ACTIVE_BREAK))
        currentBreak.checkOut = Timestamp.valueOf(LocalDateTime.now())
        currentBreak.duration = Duration.between(currentBreak.checkIn.toLocalDateTime(), LocalDateTime.now()).seconds

        attendance.apply {
            logs.removeAt(logs.size - 1)
            logs.add(currentBreak)
            breakDuration = setTimeFromSeconds(logs.sumOf { it.duration!! })
            return Response("Break ended")
        }
    }

    fun throwExceptionIfUserIsLoggedOut(attendance: Attendance) = validate(
        attendance.login == null || attendance.logout != null,
        NotAllowedException(NotAllowed(message = ALREADY_LOGGED_OUT))
    )

    fun setTimeFromSeconds(seconds: Long): Time {
        val hours = (seconds / 3600)
        val minutes = (seconds % 3600 / 60)
        return Time.valueOf("${hours}:${minutes}:${Duration.ofSeconds(seconds).toSecondsPart()}")
    }

    fun getTimeInSeconds(time: Time): Int {
        val hours = time.toLocalTime().hour
        val minutes = time.toLocalTime().minute
        val seconds = time.toLocalTime().second
        return (hours * 3600) + (minutes * 60) + seconds
    }
}