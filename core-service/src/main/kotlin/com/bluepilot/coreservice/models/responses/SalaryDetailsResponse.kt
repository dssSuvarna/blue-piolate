package com.bluepilot.coreservice.models.responses

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class SalaryDetailsResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("userId")
    val userId: Long,
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
    @JsonProperty("annualCtc")
    val annualCtc: BigDecimal
)
