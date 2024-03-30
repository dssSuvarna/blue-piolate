package com.bluepilot.userservice.models.responses

import com.bluepilot.enums.Domain
import com.bluepilot.enums.TrainingStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class TrainingDetailsResponse (
    @JsonProperty("trainingDetailsId")
    val trainingDetailsId: Long,
    @JsonProperty("userId")
    val userId: Long,
    @JsonProperty("trainerId")
    val trainerId: Long,
    @JsonProperty("domain")
    val domain: Domain? = null,
    @JsonProperty("userCourses")
    val userCourses: List<Long> = emptyList(),
    @JsonProperty("startedAt")
    val startedAt: Timestamp? = null,
    @JsonProperty("completedAt")
    val completedAt: Timestamp? = null,
    @JsonProperty("completionTime")
    val completionTime: Int? = null,
    @JsonProperty("trainingStatus")
    val trainingStatus: TrainingStatus
)