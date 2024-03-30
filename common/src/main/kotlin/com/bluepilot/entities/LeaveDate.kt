package com.bluepilot.entities

import com.bluepilot.enums.Day
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Date

data class LeaveDate(
    @JsonProperty("date")
    val date: Date,
    @JsonProperty("day")
    val day: Day = Day.FULL_DAY
)
