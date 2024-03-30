package com.bluepilot.notificationservice.services

import com.bluepilot.notifications.SqsEventProducer
import com.bluepilot.notificationservice.models.requests.EmailRequest
import jakarta.mail.internet.MimeMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EmailService(
    private val javaMailSender: JavaMailSender
) {
    val logger: Logger = LoggerFactory.getLogger(SqsEventProducer::class.java)

    fun sendEmail(emailRequest: EmailRequest) {
        try {
            logger.info("Sending email")
            val message: MimeMessage = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)

            // Set email properties
            helper.setSubject(emailRequest.subject)
            helper.setTo(emailRequest.emailTo)
            helper.setText(emailRequest.body, true)

            // Send the email
            javaMailSender.send(message)
            logger.info("Email sent")
        } catch (e: MailException) {
            logger.error("Failed to send email: ${e.message}")
        } catch (e: Exception) {
            logger.error("Failed to send email: ${e.message}")
        }
    }
}
