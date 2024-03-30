package com.bluepilot.userservice.models.requests

import com.bluepilot.enums.Domain

data class StartTrainingRequest(
    val trainingDetailsId: Long,
    val userId: Long,
    val trainerId: Long,
    val domain: Domain,
    val completionTime: Int
)
