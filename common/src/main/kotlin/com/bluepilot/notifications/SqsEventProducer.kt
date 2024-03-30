package com.bluepilot.notifications

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageResponse

@Service
class SqsEventProducer(private val sqsClient: SqsClient) {

    val logger: Logger = LoggerFactory.getLogger(SqsEventProducer::class.java)

    @Value("\${aws.sqs.queue-url}")
    private lateinit var SQS_QUEUE_URL: String

    fun produceMessage(messageBody: String): SendMessageResponse {
        var response: SendMessageResponse? = null
        try {
            logger.info("Sending notification event -> body: $messageBody")
            val sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(SQS_QUEUE_URL)
                .messageBody(messageBody)
                .build()
            response = sqsClient.sendMessage(sendMessageRequest)
        } catch (e: Exception) {
            logger.error("Sending notification event failed -> body: $messageBody")
            logger.error(e.message)
        }
        logger.info("Notification event sent -> body: $messageBody")
        return response!!
    }
}