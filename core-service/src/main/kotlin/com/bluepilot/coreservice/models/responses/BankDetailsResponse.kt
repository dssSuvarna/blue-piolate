package com.bluepilot.coreservice.models.responses

data class BankDetailsResponse(
    val accountHolderName: String,
    val accountNumber: Long,
    val ifsc: String,
    val bankName: String,
)