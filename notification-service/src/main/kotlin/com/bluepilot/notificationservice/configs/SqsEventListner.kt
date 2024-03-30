package com.bluepilot.notificationservice.configs

import com.bluepilot.entities.Notification
import com.bluepilot.enums.NotificationStatus
import com.bluepilot.errors.NotFoundError
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.notifications.Event
import com.bluepilot.notificationservice.services.EmailService
import com.bluepilot.notificationservice.services.TemplateService
import com.bluepilot.repositories.NotificationRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
@EnableScheduling
@Transactional
class SqsEventListener @Autowired constructor(
    val sqsClient: SqsClient,
    val emailService: EmailService,
    val templateService: TemplateService,
    val notificationRepository: NotificationRepository
) {
    val logger: Logger = LoggerFactory.getLogger(SqsEventListener::class.java)

    @Value("\${aws.sqs.queue-url}")
    private lateinit var SQS_QUEUE_URL: String

    @Scheduled(fixedRate = 1000)
    fun pollMessages() {
        var notification: Notification? = null
        try {
            // Receive messages from the SQS queue
            val receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(SQS_QUEUE_URL)
                .maxNumberOfMessages(10)
                .build()

            val receivedMessages = sqsClient.receiveMessage(receiveMessageRequest).messages()
            // Process received messages
            for (message in receivedMessages) {
                logger.info("Received message from queue-> $message")
                val event = ObjectMapper().readValue(message.body(), Event::class.java)
                notification =
                    notificationRepository.findByUuid(event.eventId) ?: throw NotFoundException(NotFoundError())
                notification.status = NotificationStatus.IN_PROGRESS
                logger.info("Notification processing started -> uuid:${notification.uuid}")
                val emailRequest = templateService.constructEmailRequestWithTemplate(event.eventType, event.payload)
                logger.info("Sending notification email -> uuid:${notification.uuid}")
                emailService.sendEmail(emailRequest)
                val deleteMessageRequest = DeleteMessageRequest.builder()
                    .queueUrl(SQS_QUEUE_URL)
                    .receiptHandle(message.receiptHandle())
                    .build()

                sqsClient.deleteMessage(deleteMessageRequest)
                notification.status = NotificationStatus.SENT
                logger.info("Deleted message from queue : ${message.body()}")
            }
        } catch (e: Exception) {
            try {
                logger.error(e.message)
                notification?.status = NotificationStatus.FAILED
            } catch (_: Exception){ }
            logger.error(e.message)
        }
    }
}