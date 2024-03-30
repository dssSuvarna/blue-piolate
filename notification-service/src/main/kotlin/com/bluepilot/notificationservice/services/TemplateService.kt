package com.bluepilot.notificationservice.services

import com.bluepilot.enums.NotificationEventType
import com.bluepilot.notificationservice.configs.CompanyConfiguration
import com.bluepilot.notificationservice.models.requests.EmailRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer

@Service
class TemplateService @Autowired constructor(
    val freemarkerConfigurer: FreeMarkerConfigurer,
    val companyConfiguration: CompanyConfiguration
) {

    fun constructEmailRequestWithTemplate(
        eventType: NotificationEventType,
        payload: Map<String, String?>
    ): EmailRequest {
        val template = freemarkerConfigurer.configuration.getTemplate(eventType.template.template)
        val commonPlaceHolders = companyConfiguration.getPlaceHolderValues()
        val templateBody = FreeMarkerTemplateUtils.processTemplateIntoString(
            template, payload.plus(commonPlaceHolders)
        )
        return EmailRequest(
            emailTo = payload["emailTo"]!!,
            subject = eventType.template.subject,
            body = templateBody
        )
    }
}