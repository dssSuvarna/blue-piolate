package com.bluepilot.coreservice.models.requests

import jakarta.validation.constraints.NotNull

data class UnAssignUserSystemResourceRequest(
    @field:NotNull
    val userId: Long,
    @field:NotNull
    val systemResourceId: Long,
)