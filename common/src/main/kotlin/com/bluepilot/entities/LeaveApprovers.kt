package com.bluepilot.entities

import com.bluepilot.enums.LeaveStatus
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "leave_approvers", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class LeaveApprover(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,
    @Enumerated(EnumType.STRING)
    var status: LeaveStatus
)
