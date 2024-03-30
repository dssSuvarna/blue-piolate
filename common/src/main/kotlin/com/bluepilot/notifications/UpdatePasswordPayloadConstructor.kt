package com.bluepilot.notifications

import com.bluepilot.enums.NotificationEventType
import com.bluepilot.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UpdatePasswordPayloadConstructor @Autowired constructor(
    val userRepository: UserRepository
) : NotificationPayloadConstructor {

    override fun constructPayload(id: Long?, additionalData: Map<String, String?>): Map<String, String?> {
        val user = userRepository.findById(id!!).get()
        return mapOf(
            "emailTo" to user.userDetails!!.professionalEmail,
        )
    }

    override fun getNotificationType() = NotificationEventType.UPDATED_PASSWORD
}