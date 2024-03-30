package com.bluepilot.repositories

import com.bluepilot.entities.Attendance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.sql.Date

@Repository
interface AttendanceRepository : JpaRepository<Attendance, Long> {

    fun findByDateAndUserId(date: Date, userId: Long): Attendance?
}