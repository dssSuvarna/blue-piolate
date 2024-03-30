package com.bluepilot.notifications

import com.bluepilot.enums.NotificationEventType
import org.springframework.stereotype.Service

@Service
class OtpPayloadConstructor : NotificationPayloadConstructor {

    override fun constructPayload(id: Long?, additionalData: Map<String, String?>): Map<String, String?> {
        return additionalData
    }

    override fun getNotificationType() = NotificationEventType.OTP_TEMPLATE
}