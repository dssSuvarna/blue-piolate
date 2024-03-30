package com.bluepilot.notifications

import com.bluepilot.entities.Notification
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.NotificationStatus
import com.bluepilot.repositories.NotificationRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class EventService @Autowired constructor(
    val sqsEventProducer: SqsEventProducer,
    val payloadConstructors: List<NotificationPayloadConstructor>,
    val notificationRepository: NotificationRepository
) {

    val logger: Logger = LoggerFactory.getLogger(SqsEventProducer::class.java)

    fun sendEvent(
        id: Long? = null,
        notificationEventType: NotificationEventType,
        additionalData: Map<String, String?> = emptyMap()
    ) {
        logger.info("Processing event-> notificationType: $notificationEventType")
        val payloadConstructor = payloadConstructors.firstOrNull {
            it.getNotificationType() == notificationEventType
        }
        val payload = payloadConstructor!!.constructPayload(id, additionalData)
        val event = Event(
            eventId = UUID.randomUUID(),
            eventType = notificationEventType,
            payload = payload
        )
        saveNotificationEvent(event)
        processEvent(event)
    }

    fun processEvent(event: Event) {
        sqsEventProducer.produceMessage(
            ObjectMapper().writeValueAsString(event)
        )
    }

    fun saveNotificationEvent(event: Event) {
        logger.info("Saving notification -> uuid: ${event.eventId}")
        notificationRepository.save(
            Notification(
                uuid = event.eventId,
                status = NotificationStatus.CREATED,
                payload = event.payload,
                notificationEventType = event.eventType
            )
        )
        logger.info("Notification saved -> uuid: ${event.eventId}")
    }
}