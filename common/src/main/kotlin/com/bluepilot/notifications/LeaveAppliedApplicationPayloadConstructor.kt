package com.bluepilot.notifications

import com.bluepilot.enums.NotificationEventType
import org.springframework.stereotype.Service

@Service
class LeaveAppliedApplicationPayloadConstructor : NotificationPayloadConstructor {

    override fun constructPayload(id: Long?, additionalData: Map<String, String?>): Map<String, String?> {
        return additionalData
    }

    override fun getNotificationType() = NotificationEventType.LEAVE_APPLIED_APPLICATION
}