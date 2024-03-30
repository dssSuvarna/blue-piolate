package com.bluepilot.coreservice.models.requests

import com.bluepilot.entities.MonthHolidays
import jakarta.validation.constraints.NotNull

data class UpdateHolidayListRequest (
    var id: Long = 0,
    @field:NotNull
    val year: Int,
    @field:NotNull
    val holidays: List<MonthHolidays>
)