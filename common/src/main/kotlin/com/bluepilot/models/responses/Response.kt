package com.bluepilot.models.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class Response(
    @JsonProperty("response")
    val response: String
)