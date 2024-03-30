package com.bluepilot.entities

import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedDate
import java.sql.Date

@Entity
@Table(name = "leaves", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class Leave(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    val leaveDates: List<LeaveDate>,
    @Enumerated(EnumType.STRING)
    var status: LeaveStatus,
    @Enumerated(EnumType.STRING)
    val leaveType: LeaveType,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "leaves_approval_from",
        schema = DataBaseUtils.SCHEMA.USER_SERVICE,
        joinColumns = [JoinColumn(name = "leave_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "approval_from_id", referencedColumnName = "id")]
    )
    var approvalFrom: MutableList<LeaveApprover>,
    @CreatedDate
    val appliedDate: Date,
    val reason: String
) {
    fun getTotalLeaveDays() = leaveDates.sumOf { it.day.value }
}