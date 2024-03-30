package com.bluepilot.coreservice.models.requests

import java.math.BigDecimal

data class EmployeeSalaryUpdateRequest(
    val empSalaryId: Long,
    val basic: BigDecimal? = null,
    val hra: BigDecimal? = null,
    val specialAllowances: BigDecimal? = null,
    val performanceIncentive: BigDecimal? = null,
    var oneTimeIncentive: BigDecimal? = null,
    val pt: BigDecimal? = null,
    val it: BigDecimal? = null,
    val pf: BigDecimal? = null,
    val esi: BigDecimal? = null,
    val advance: BigDecimal? = null,
    val bonus: BigDecimal? = null
)