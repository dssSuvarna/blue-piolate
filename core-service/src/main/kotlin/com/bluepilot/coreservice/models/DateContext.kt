package com.bluepilot.coreservice.models

import com.bluepilot.enums.Month
import java.math.BigDecimal
import java.time.ZonedDateTime

data class DateContext(
    val previousMonth: Month,
    val daysInMonth: BigDecimal,
    val year: Int,
    val zonedDateTimeOfPreviousMonth: ZonedDateTime
)