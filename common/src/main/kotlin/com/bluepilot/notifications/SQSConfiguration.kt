package com.bluepilot.notifications

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
class SQSConfiguration {

    @Value("\${aws.sqs.profile}")
    private lateinit var profile: String

    @Value("\${aws.sqs.region}")
    private lateinit var region: String

    @Value("\${aws.sqs.endpoint}")
    private lateinit var endPoint: String

    @Bean
    fun sqsClient(): SqsClient {
        val credentialsProvider: AwsCredentialsProvider = ProfileCredentialsProvider.builder()
            .profileName(profile)
            .build()

        return SqsClient.builder()
            .region(Region.of(region))
            .endpointOverride(URI.create(endPoint))
            .credentialsProvider(credentialsProvider)
            .build()
    }
}