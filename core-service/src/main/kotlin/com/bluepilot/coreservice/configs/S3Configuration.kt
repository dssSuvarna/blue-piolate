package com.bluepilot.coreservice.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class S3Configuration {

    @Value("\${aws.s3.profile}")
    private lateinit var profile: String

    @Value("\${aws.s3.region}")
    private lateinit var region: String

    @Value("\${aws.s3.endpoint}")
    private lateinit var endPoint: String

    @Bean
    fun s3Client(): S3Client {
        val credentialsProvider: AwsCredentialsProvider = ProfileCredentialsProvider.builder()
            .profileName(profile) // Specify the AWS profile name from the credentials file
            .build()

        return S3Client.builder()
            .region(Region.of(region))
            .endpointOverride(URI.create(endPoint))
            .credentialsProvider(credentialsProvider)
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        return  S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }
}