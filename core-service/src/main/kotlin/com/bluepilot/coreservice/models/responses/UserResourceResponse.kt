package com.bluepilot.coreservice.models.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class UserResourceResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("idCard")
    val idCard: String,
    @JsonProperty("professionalEmail")
    val professionalEmail: String,
    @JsonProperty("systemResourcesResponse")
    val systemResourcesResponse: SystemResourcesResponse ?
)