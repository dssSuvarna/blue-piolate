package com.bluepilot.notifications

import com.bluepilot.enums.NotificationEventType
import org.springframework.stereotype.Service

@Service
class OnboardedEmployeePayloadConstructor : NotificationPayloadConstructor {
    override fun constructPayload(id: Long?, additionalData: Map<String, String?>): Map<String, String?> {
        return additionalData
    }

    override fun getNotificationType(): NotificationEventType {
        return NotificationEventType.WELCOME_ONBOARDED_EMPLOYEE
    }
}