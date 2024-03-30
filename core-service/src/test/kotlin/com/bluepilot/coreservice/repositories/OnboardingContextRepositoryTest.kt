package com.bluepilot.coreservice.repositories

import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.entities.OnboardingContext
import com.bluepilot.enums.OnboardingContextStatus
import com.bluepilot.repositories.OnboardingContextRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OnboardingContextRepositoryTest @Autowired constructor(
    private val onboardingContextRepository: OnboardingContextRepository
) : BaseTestConfig() {
    @Test
    fun shouldSaveInviteDetails() {
        val inviteRequest = OnboardingContext(
            personalEmail = "Test1@Test",
            inviteCode = "TestCode",
            onboardingContextStatus = OnboardingContextStatus.INVITED
        )
        val savedEntity = onboardingContextRepository.save(inviteRequest)
        Assertions.assertThat(savedEntity).isNotNull
        Assertions.assertThat(savedEntity.personalEmail).isEqualTo(inviteRequest.personalEmail)
    }

    @Test
    fun shouldGetUserByMail() {
        val inviteRequest = OnboardingContext(
            personalEmail = "Test2@Test",
            inviteCode = "TestCode",
            onboardingContextStatus = OnboardingContextStatus.INVITED
        )
        onboardingContextRepository.save(inviteRequest)
        val foundUser = onboardingContextRepository.findByPersonalEmail(inviteRequest.personalEmail)

        Assertions.assertThat(foundUser?.personalEmail).isNotNull()
    }
}