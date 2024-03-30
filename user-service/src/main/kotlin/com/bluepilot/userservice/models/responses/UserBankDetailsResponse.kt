package com.bluepilot.userservice.models.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class UserBankDetailsResponse (
    @JsonProperty("accountNumber")
    val accountNumber: Long,
    @JsonProperty("ifsc")
    val ifsc: String,
    @JsonProperty("bankName")
    val bankName: String,
    @JsonProperty("accountHolderName")
    val accountHolderName: String,
)