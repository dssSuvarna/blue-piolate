package com.bluepilot.coreservice.models.responses

import com.bluepilot.enums.EmployeeSalaryStatus
import com.bluepilot.enums.Month
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.math.BigDecimal
import java.sql.Date

data class EmployeeSalaryResponse (
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("userId")
    val userId: Long,
    @JsonProperty("designation")
    val designation: String,
    @JsonProperty("empCode")
    val empCode: String,
    @JsonProperty("doj")
    val doj: Date,
    @JsonProperty("basic")
    val basic: BigDecimal,
    @JsonProperty("hra")
    val hra: BigDecimal,
    @JsonProperty("specialAllowances")
    val specialAllowances: BigDecimal,
    @JsonProperty("performanceIncentive")
    val performanceIncentive: BigDecimal,
    @JsonProperty("pt")
    val pt: BigDecimal,
    @JsonProperty("it")
    val it: BigDecimal,
    @JsonProperty("pf")
    val pf: BigDecimal,
    @JsonProperty("esi")
    val esi: BigDecimal,
    @JsonProperty("employeeCode")
    val employeeCode: String,
    @JsonProperty("panNo")
    val panNo: String,
    @Enumerated(EnumType.STRING)
    @JsonProperty("month")
    val month: Month,
    @JsonProperty("year")
    val year: Int,
    @JsonProperty("dol")
    var dol: Date? = null,
    @JsonProperty("totalWorkingDays")
    val totalWorkingDays: BigDecimal,
    @JsonProperty("totalPayableDays")
    val totalPayableDays: BigDecimal,
    @JsonProperty("oneTimeIncentive")
    var oneTimeIncentive: BigDecimal,
    @JsonProperty("advance")
    val advance: BigDecimal,
    @JsonProperty("grossEarning")
    val grossEarning: BigDecimal,
    @JsonProperty("grossDeductions")
    val grossDeductions: BigDecimal,
    @JsonProperty("grossPay")
    val grossPay: BigDecimal,
    @JsonProperty("ytdBasic")
    val ytdBasic: BigDecimal,
    @JsonProperty("ytdHra")
    val ytdHra: BigDecimal,
    @JsonProperty("ytdSpecialAllowances")
    val ytdSpecialAllowances: BigDecimal,
    @JsonProperty("ytdBonus")
    val ytdBonus: BigDecimal,
    @JsonProperty("ytdEarnings")
    val ytdEarnings: BigDecimal,
    @JsonProperty("ytdPt")
    val ytdPt: BigDecimal,
    @JsonProperty("ytdPf")
    val ytdPf: BigDecimal,
    @JsonProperty("ytdEsi")
    val ytdEsi: BigDecimal,
    @JsonProperty("ytdOtherDeductions")
    val ytdOtherDeductions: BigDecimal,
    @JsonProperty("ytdIt")
    val ytdIt: BigDecimal,
    @JsonProperty("ytdDeductions")
    val ytdDeductions: BigDecimal,
    @JsonProperty("ytdPmEarnings")
    val ytdPmEarnings: BigDecimal,
    @JsonProperty("ytdPmBasic")
    val ytdPmBasic: BigDecimal,
    @JsonProperty("ytdPmHra")
    val ytdPmHra: BigDecimal,
    @JsonProperty("ytdPmSpecialAllowances")
    val ytdPmSpecialAllowances: BigDecimal,
    @JsonProperty("ytdPmBonus")
    val ytdPmBonus: BigDecimal,
    @JsonProperty("ytdPmDeductions")
    val ytdPmDeductions: BigDecimal,
    @JsonProperty("ytdPmPt")
    val ytdPmPt: BigDecimal,
    @JsonProperty("ytdPmPf")
    val ytdPmPf: BigDecimal,
    @JsonProperty("ytdPmEsi")
    val ytdPmEsi: BigDecimal,
    @JsonProperty("ytdPmIt")
    val ytdPmIt: BigDecimal,
    @JsonProperty("ytdPmOtherDeductions")
    val ytdPmOtherDeductions: BigDecimal,
    @JsonProperty("status")
    val status: EmployeeSalaryStatus
)