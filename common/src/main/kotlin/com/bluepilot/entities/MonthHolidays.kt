package com.bluepilot.entities

import com.fasterxml.jackson.annotation.JsonProperty

data class MonthHolidays(
    @JsonProperty("month")
    val month: String,
    @JsonProperty("holidays")
    val holidays: List<Holiday>
)