package com.bluepilot.coreservice.models.requests

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class UpdateSalaryDetailsRequest(
    @field:NotNull
    val userId: Long,
    val pt: BigDecimal,
    val it: BigDecimal,
    val pf: BigDecimal,
    val esi: BigDecimal,
)