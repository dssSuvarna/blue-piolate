package com.bluepilot.entities

import com.bluepilot.enums.ProgressStatus
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "user_course", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class UserCourse(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinColumn(name = "training_details", referencedColumnName = "id")
    val trainingDetails: TrainingDetails,
    @ManyToOne(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinColumn(name = "course", referencedColumnName = "id")
    val course: Courses,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "user_course", referencedColumnName = "id")
    var userContent: List<UserContent>,
    @Enumerated(EnumType.STRING)
    var status: ProgressStatus,
    var startedAt: Timestamp? = null,
    val completedAt: Timestamp? = null,
    var hoursSpent: Int = 0
)