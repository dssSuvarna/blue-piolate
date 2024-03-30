package com.bluepilot.notificationservice.models.requests

import com.fasterxml.jackson.annotation.JsonProperty

data class EmailRequest(
    @JsonProperty("to")
    val emailTo: String,
    @JsonProperty("subject")
    val subject: String,
    @JsonProperty("body")
    var body: String
)