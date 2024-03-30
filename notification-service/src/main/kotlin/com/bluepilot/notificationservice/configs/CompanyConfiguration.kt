package com.bluepilot.notificationservice.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class CompanyConfiguration {
    @Value("\${config.company-name}")
    lateinit var companyName: String

    @Value("\${config.company-link}")
    lateinit var companyLink: String

    @Value("\${config.company-logo}")
    lateinit var companyLogo: String

    @Value("\${config.linkedin-link}")
    lateinit var linkedinLink: String

    @Value("\${config.linkedin-logo}")
    lateinit var linkedinLogo: String

    @Value("\${config.instagram-link}")
    lateinit var instagramLink: String

    @Value("\${config.instagram-logo}")
    lateinit var instagramLogo: String

    @Value("\${config.company-address}")
    lateinit var companyAddress: String

    @Value("\${config.company-email}")
    lateinit var companyEmail: String

    fun getPlaceHolderValues(): Map<String, String> = mapOf(
        "companyName" to companyName.uppercase(),
        "companyLink" to companyLink,
        "companyLogo" to companyLogo,
        "linkedinLink" to linkedinLink,
        "linkedinLogo" to linkedinLogo,
        "instagramLink" to instagramLink,
        "instagramLogo" to instagramLogo,
        "companyAddress" to companyAddress,
        "companyEmail" to companyEmail
    )
} 