package com.bluepilot.entities

import com.bluepilot.enums.Domain
import com.bluepilot.enums.TrainingStatus
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "training_details", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class TrainingDetails(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    var trainerId: Long,
    @Enumerated(EnumType.STRING)
    var domain: Domain? = null,
    @OneToMany(mappedBy = "trainingDetails")
    var courses: List<UserCourse> = mutableListOf(),
    var startedAt: Timestamp? = null,
    var completedAt: Timestamp? = null,
    var completionTime: Int? = null,
    @Enumerated(EnumType.STRING)
    var status: TrainingStatus = TrainingStatus.NOT_STARTED
)
