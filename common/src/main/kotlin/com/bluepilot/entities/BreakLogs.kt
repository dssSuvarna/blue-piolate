package com.bluepilot.entities

import java.sql.Timestamp
import java.time.Instant

data class BreakLogs(
    val checkIn: Timestamp = Timestamp.from(Instant.now()),
    var checkOut: Timestamp? = null,
    var duration: Long? = null
)
