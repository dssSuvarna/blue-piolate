package com.bluepilot.notifications

import com.bluepilot.enums.NotificationEventType
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Event(
    @JsonProperty("eventId")
    val eventId: UUID,
    @JsonProperty("eventType")
    val eventType: NotificationEventType,
    @JsonProperty("payload")
    val payload: Map<String, String?>,
)

