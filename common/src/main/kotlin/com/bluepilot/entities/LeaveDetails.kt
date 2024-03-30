package com.bluepilot.entities

import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "leave_details", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class LeaveDetails(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var totalLeaves: BigDecimal,
    var appliedLeaves: BigDecimal = BigDecimal(0.0),
    var pendingLeaves: BigDecimal = (totalLeaves - appliedLeaves),
    var totalSickLeave: BigDecimal,
    var pendingSickLeave: BigDecimal = totalSickLeave,
    var totalPrivilegeLeave: BigDecimal,
    var pendingPrivilegeLeave: BigDecimal = totalPrivilegeLeave,
    var totalCompOffLeave: BigDecimal = BigDecimal(0.0)
)
