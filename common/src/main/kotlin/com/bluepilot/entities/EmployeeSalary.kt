package com.bluepilot.entities

import com.bluepilot.enums.EmployeeSalaryStatus
import com.bluepilot.enums.Month
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.sql.Date
import java.time.Instant

@Entity
@Table(name = "employee_salary", schema = DataBaseUtils.SCHEMA.SALARY)
data class EmployeeSalary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    val employeeCode: String,
    val designation: String,
    val panNo: String,
    @Enumerated(EnumType.STRING)
    val month: Month,
    val year: Int,
    val doj: Date,
    var dol: Date? = null,
    val totalWorkingDays: BigDecimal,
    var totalPayableDays: BigDecimal,
    var basic: BigDecimal,
    var hra: BigDecimal,
    var specialAllowances: BigDecimal,
    var performanceIncentive: BigDecimal,
    var oneTimeIncentive: BigDecimal,
    var pt: BigDecimal,
    var it: BigDecimal,
    var pf: BigDecimal,
    var esi: BigDecimal,
    var advance: BigDecimal,
    var grossEarning: BigDecimal,
    var grossDeductions: BigDecimal,
    var grossPay: BigDecimal,
    var ytdBasic: BigDecimal,
    var ytdHra: BigDecimal,
    var ytdSpecialAllowances: BigDecimal,
    var ytdBonus: BigDecimal,
    var ytdEarnings: BigDecimal,
    var ytdPt: BigDecimal,
    var ytdPf: BigDecimal,
    var ytdEsi: BigDecimal,
    var ytdIt: BigDecimal,
    var ytdOtherDeductions: BigDecimal,
    var ytdDeductions: BigDecimal,
    val ytdPmEarnings: BigDecimal,
    val ytdPmBasic: BigDecimal,
    val ytdPmHra: BigDecimal,
    val ytdPmSpecialAllowances: BigDecimal,
    val ytdPmBonus: BigDecimal,
    val ytdPmDeductions: BigDecimal,
    val ytdPmPt: BigDecimal,
    val ytdPmPf: BigDecimal,
    val ytdPmEsi: BigDecimal,
    val ytdPmIt: BigDecimal,
    val ytdPmOtherDeductions: BigDecimal,
    @CreationTimestamp
    val createdAt: Date = Date(Instant.now().toEpochMilli()),
    @UpdateTimestamp
    var updatedAt: Date = Date(Instant.now().toEpochMilli()),
    @Enumerated(EnumType.STRING)
    var status: EmployeeSalaryStatus = EmployeeSalaryStatus.TO_BE_VERIFIED
)