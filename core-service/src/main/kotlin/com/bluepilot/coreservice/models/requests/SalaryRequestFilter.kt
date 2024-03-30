package com.bluepilot.coreservice.models.requests

import com.bluepilot.enums.Month

data class SalaryRequestFilter(
    val month: Month? = null,
    val userId: Long? = null
)