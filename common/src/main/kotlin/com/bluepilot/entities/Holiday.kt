package com.bluepilot.entities

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.sql.Date

data class Holiday(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("date")
    val date: Date,
    @JsonProperty("description")
    val description: String
)
