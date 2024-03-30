package com.bluepilot.authservice.services

import com.bluepilot.authservice.BaseTestConfig
import com.bluepilot.authservice.models.requests.ResetPasswordRequest
import com.bluepilot.configs.JwtService
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthUserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class AuthServiceTest @Autowired constructor(
    val otpService: OtpService,
    val authService: AuthService,
    val authUserRepository: AuthUserRepository,
    val passwordEncoder: PasswordEncoder
) : BaseTestConfig() {


    @MockBean
    lateinit var eventService: EventService

    @BeforeEach
    fun saveOnboardingContext() {
        Mockito.`when`(eventService.processEvent(com.bluepilot.utils.Mockito.anyObject())).then { }
    }


    @Test
    fun resetPasswordTest() {
        val otp = otpService.generateAndStoreOtp(username = "admin@gmail.com")
        Assertions.assertEquals(otpService.isOtpValid(email = "admin@gmail.com", otp = otp), true)
        val token = JwtService.generateToken(authUserRepository.findByUsername("admin@gmail.com")!!)
        val resetPasswordRequest = ResetPasswordRequest(
            newPassword = "Test@123",
            confirmPassword = "Test@123"
        )
        authService.resetPassword(
            resetPasswordRequest,
            token
        )
        val authUser = authUserRepository.findByUsername("admin@gmail.com")!!
        Assertions.assertEquals(true, passwordEncoder.matches(resetPasswordRequest.newPassword, authUser.password))
    }
}