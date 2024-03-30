package com.bluepilot.entities

import com.bluepilot.enums.AttendanceStatus
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.util.UUID

@Entity
@Table(name = "attendance", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class Attendance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val uuid: UUID = UUID.randomUUID(),
    val userId: Long,
    val date: Date,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    var logs: MutableList<BreakLogs> = mutableListOf(),
    val login: Timestamp? = null,
    var breakDuration: Time = Time.valueOf("00:00:00"),
    var logout: Timestamp? = null,
    var loginHours: Time = Time.valueOf("00:00:00"),
    @Enumerated(EnumType.STRING)
    val status: AttendanceStatus
)
