package com.bluepilot.entities

import com.bluepilot.enums.ProgressStatus
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "user_content", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class UserContent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "content", referencedColumnName = "id")
    val content: Contents,
    @Enumerated(EnumType.STRING)
    var status: ProgressStatus,
    var startedAt: Timestamp? = null,
    var completedAt: Timestamp? = null,
    var pointsAwarded: Int = 0,
)