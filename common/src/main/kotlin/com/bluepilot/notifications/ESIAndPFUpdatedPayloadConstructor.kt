package com.bluepilot.notifications

import com.bluepilot.enums.NotificationEventType
import com.bluepilot.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ESIAndPFUpdatedPayloadConstructor @Autowired constructor(
    val userRepository: UserRepository
) : NotificationPayloadConstructor {

    override fun constructPayload(id: Long?, additionalData: Map<String, String?>): Map<String, String?> {
        return additionalData
    }

    override fun getNotificationType() = NotificationEventType.ESI_AND_PF_UPDATE
}