package com.bluepilot.coreservice.models.requests

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class AddSalaryDetailsRequest (
    @field:NotNull
    val userId: Long,
    @field:NotNull
    val basic: BigDecimal,
    @field:NotNull
    val hra: BigDecimal,
    @field:NotNull
    val specialAllowances: BigDecimal,
    var performanceIncentive: BigDecimal = BigDecimal(0.0),
    @field:NotNull
    val pt: BigDecimal,
    @field:NotNull
    val it: BigDecimal,
    @field:NotNull
    val pf: BigDecimal,
    @field:NotNull
    val esi: BigDecimal,
    @field:NotNull
    val annualCtc: BigDecimal
)