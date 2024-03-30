package com.bluepilot.userservice.models.requests

import com.bluepilot.enums.Domain

data class UpdateTrainingDetailsRequest (
    val trainingDetailsId: Long,
    val trainerId: Long,
    val domain: Domain,
    val completionTime: Int
)