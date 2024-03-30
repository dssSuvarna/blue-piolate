package com.bluepilot.entities

import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "holiday_list", schema = DataBaseUtils.SCHEMA.CORE_SERVICE)
data class HolidayList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val year: Int,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    var holidays: List<MonthHolidays> = emptyList()
)
