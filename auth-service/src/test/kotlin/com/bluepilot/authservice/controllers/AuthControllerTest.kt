package com.bluepilot.authservice.controllers

import com.bluepilot.authservice.BaseTestConfig
import com.bluepilot.authservice.models.requests.AuthRequest
import com.bluepilot.authservice.models.requests.GenerateOtpRequest
import com.bluepilot.authservice.models.requests.ResetPasswordRequest
import com.bluepilot.authservice.models.requests.VerificationRequest
import com.bluepilot.authservice.models.responses.LoginResponse
import com.bluepilot.authservice.models.responses.OtpVerificationResponse
import com.bluepilot.authservice.models.responses.UserRolesPermissionsResponse
import com.bluepilot.authservice.services.OtpService
import com.bluepilot.configs.JwtService
import com.bluepilot.entities.AuthUser
import com.bluepilot.entities.User
import com.bluepilot.enums.AuthUserStatus
import com.bluepilot.enums.Role
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthUserRepository
import com.bluepilot.repositories.RoleRepository
import com.bluepilot.repositories.UserRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class AuthControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val passwordEncoder: PasswordEncoder,
    val otoService: OtpService,
    val authUserRepository: AuthUserRepository
) : BaseTestConfig() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var eventService: EventService


    @BeforeEach
    fun saveOnboardingContext() {
        Mockito.`when`(eventService.processEvent(com.bluepilot.utils.Mockito.anyObject())).then { }
    }

    @Test
    fun shouldGetLoginResponse() {
        val adminRole = roleRepository.findById(1).get()
        userRepository.save(
            User(
                firstName = "testing",
                lastName = "testing",
                employeeCode = "PGN1",
                designation = "designation",
                authUser = AuthUser(
                    username = "admin@test.com",
                    password = passwordEncoder.encode("Test@123"),
                    status = AuthUserStatus.ENABLED,
                    role = adminRole
                ),
                userDetails = null,
                reporter = null
            )
        )

        val authReq = AuthRequest(username = "admin@test.com", password = "Test@123")
        val result = mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(authReq)
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isAccepted() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            LoginResponse::class.java
        )!!

        val username = JwtService.extractUsername(response.token)
        Assertions.assertEquals(authReq.username, username)
    }

    @Test
    fun shouldNotLoginDisabledUserResponse() {
        val employeeRole = roleRepository.findByName(Role.EMPLOYEE)
        val user = userRepository.save(
            User(
                firstName = "testing",
                lastName = "testing",
                employeeCode = "PGN1",
                designation = "designation",
                authUser = AuthUser(
                    username = "employee@test.com",
                    password = passwordEncoder.encode("Test@123"),
                    status = AuthUserStatus.ENABLED,
                    role = employeeRole
                ),
                userDetails = null,
                reporter = null
            )
        )
        user.authUser.status = AuthUserStatus.DISABLED
        userRepository.save(user)
        val authReq = AuthRequest(username = "employee@test.com", password = "Test@123")
        mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(authReq)
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isUnauthorized() } }
    }

    @Test
    fun shouldGetRoleOfUser() {
        val adminRole = roleRepository.findById(1).get()
        val user = userRepository.save(
            User(
                firstName = "testing",
                lastName = "testing",
                employeeCode = "PGN1",
                designation = "designation",
                authUser = AuthUser(
                    username = "admin@test.com",
                    password = passwordEncoder.encode("Test@123"),
                    status = AuthUserStatus.ENABLED,
                    role = adminRole
                ),
                userDetails = null,
                reporter = null
            )
        )

        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

        val result = mockMvc.get("/role") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<UserRolesPermissionsResponse>() {}
        )!!

        Assertions.assertEquals(user.authUser.role.name, response.rolePermissions.role)
        Assertions.assertEquals(
            true,
            response.rolePermissions.permissions.containsAll(user.authUser.role.permissions.map { it.name })
        )
        Assertions.assertEquals(user.id, response.userId)
    }

    @Test
    fun passwordResetFlowTest() {
        val generateOtpRequest = GenerateOtpRequest(
            username = "admin@gmail.com"
        )
        mockMvc.post("/generate-otp") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(generateOtpRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }

        val otp = otoService.generateAndStoreOtp(username = "admin@gmail.com")

        val verifyRequest = VerificationRequest(
            username = "admin@gmail.com",
            otp = otp
        )

        val result = mockMvc.post("/verify-otp") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(verifyRequest)
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        val response = ObjectMapper().readValue(result.response.contentAsString, OtpVerificationResponse::class.java)
        val resetPasswordRequest = ResetPasswordRequest(
            newPassword = "Test@123",
            confirmPassword = "Test@123"
        )

       val token = "Bearer ${response.token}"
        mockMvc.post("/reset-password") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            content = ObjectMapper().writeValueAsString(resetPasswordRequest)
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        val authUser = authUserRepository.findByUsername("admin@gmail.com")
        Assertions.assertEquals(passwordEncoder.matches("Test@123", authUser!!.password), true)
    }

}