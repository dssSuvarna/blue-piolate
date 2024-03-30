package com.bluepilot.notifications

import com.bluepilot.enums.NotificationEventType

interface NotificationPayloadConstructor {

    fun constructPayload(id: Long? = null, additionalData: Map<String, String?> = emptyMap()): Map<String, String?>

    fun getNotificationType(): NotificationEventType
}