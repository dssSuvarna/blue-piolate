package com.bluepilot.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("api-config")
class ApiConfig {
    lateinit var openApis: List<String>
}