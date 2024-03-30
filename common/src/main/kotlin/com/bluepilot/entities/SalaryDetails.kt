package com.bluepilot.entities

import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import java.math.BigDecimal
import java.sql.Date
import java.time.Instant

@Entity
@Table(name = "salary_details", schema = DataBaseUtils.SCHEMA.SALARY)
data class SalaryDetails(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    val basic: BigDecimal,
    val hra: BigDecimal,
    val specialAllowances: BigDecimal,
    val performanceIncentive: BigDecimal,
    val pt: BigDecimal,
    val it: BigDecimal,
    val pf: BigDecimal,
    val esi: BigDecimal,
    val annualCtc: BigDecimal,
    @CreatedDate
    val createdAt: Date = Date(Instant.now().toEpochMilli())
)