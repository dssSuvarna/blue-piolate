package com.bluepilot.userservice.models.requests

import com.bluepilot.enums.Domain
import com.bluepilot.enums.TrainingStatus

data class TrainingDetailsRequestFilter(
    val domain: Domain? = null,
    val status: TrainingStatus? = null
)
