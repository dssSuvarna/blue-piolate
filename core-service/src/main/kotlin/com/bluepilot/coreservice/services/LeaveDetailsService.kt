package com.bluepilot.coreservice.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.ZonedDateTime
import kotlin.math.roundToInt

@Service
class LeaveDetailsService {

    @Value("\${config.annual-leaves}")
    private lateinit var annualLeaves: String

    @Value("\${config.privilege-leaves}")
    private lateinit var privilegeLeaves: String

    fun getTotalLeaves(): BigDecimal {
        val leaves = ((getMonthsForLeaves()) * annualLeaves.toDouble()) / 12.0
        return leaves.roundToInt().toBigDecimal()
    }

    fun getTotalSickLeaves(): BigDecimal {
        return getTotalLeaves() - getTotalPrivilegeLeaves()
    }

    fun getTotalPrivilegeLeaves(): BigDecimal {
        return (getMonthsForLeaves() * (privilegeLeaves.toDouble() / 12.0)).roundToInt().toBigDecimal()
    }

    fun getMonthsForLeaves(): Int =
        12 - if (ZonedDateTime.now().dayOfMonth <= 15)
            ZonedDateTime.now().month.value - 1
        else ZonedDateTime.now().month.value
}