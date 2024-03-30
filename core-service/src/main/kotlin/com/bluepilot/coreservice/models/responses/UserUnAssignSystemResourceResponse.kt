package com.bluepilot.coreservice.models.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class UserUnAssignSystemResourceResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("systemId")
    val systemId: String,
    @JsonProperty("message")
    val message: String
)