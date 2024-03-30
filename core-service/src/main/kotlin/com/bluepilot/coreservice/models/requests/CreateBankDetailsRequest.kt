package com.bluepilot.coreservice.models.requests

import jakarta.validation.constraints.NotNull

data class CreateBankDetailsRequest(
    @field:NotNull
    val accountNumber: Long,
    @field:NotNull
    val ifsc: String,
    @field:NotNull
    val bankName: String,
    @field:NotNull
    val accountHolderName: String
)