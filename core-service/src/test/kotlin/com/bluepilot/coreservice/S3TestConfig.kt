package com.bluepilot.coreservice

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import java.util.function.Supplier

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class S3TestConfig : BaseTestConfig() {
    companion object {
        @Container
        private val localStackContainer = LocalStackContainer(DockerImageName
            .parse("localstack/localstack:0.12.13"))
            .apply {
                withServices(LocalStackContainer.Service.S3)
                    .start()
            }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("aws.s3.region", localStackContainer::getRegion)
            registry.add("aws.s3.endpoint",
                Supplier { localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3)})
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
        }

        fun createBucketOnStart(){
            val s3Client = S3Client.builder().region(Region.of(localStackContainer.region))
                .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3)).build()
            s3Client.createBucket(CreateBucketRequest.builder().bucket("blue-pilot").build())
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            localStackContainer.stop()
        }

        @BeforeAll
        @JvmStatic
        fun startContainer() {
            localStackContainer.start()
            createBucketOnStart()
        }
    }
}