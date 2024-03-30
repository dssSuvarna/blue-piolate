package com.bluepilot.coreservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.coreservice.generators.BasicDetailsRequestGenerator
import com.bluepilot.coreservice.generators.OnboardedContextGenerator
import com.bluepilot.coreservice.models.responses.BasicDetailsResponse
import com.bluepilot.coreservice.models.responses.OnboardingContextResponse
import com.bluepilot.coreservice.services.OnboardingContextService
import com.bluepilot.entities.AuthUser
import com.bluepilot.entities.User
import com.bluepilot.enums.AuthUserStatus
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.OnboardingContextStatus
import com.bluepilot.enums.Role
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.OnboardingContextRepository
import com.bluepilot.repositories.UserRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
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
import org.springframework.test.web.servlet.put

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class OnboardingContextControllerTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val authRoleRepository: AuthRoleRepository,
    private val onboardingContextRepository: OnboardingContextRepository,
    private val onboardingContextService: OnboardingContextService,
    private val passwordEncoder: PasswordEncoder

) : BaseTestConfig() {
    @Autowired
    lateinit var mockMvc: MockMvc

    val mockOnboardingContextService: OnboardingContextService = Mockito.mock(OnboardingContextService::class.java)

    @MockBean
    lateinit var eventService: EventService

    @Test
    fun shouldInviteUser() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        Mockito.`when`(eventService.sendEvent(null, NotificationEventType.RE_INVITE_USER, emptyMap())).then {  }
        mockMvc.post("/admin/user-context/invite") {
            param("email", "test@gmail.com")
            param("name", "firstname")
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }
    }

    @Test
    fun shouldReInviteUser() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        Mockito.`when`(eventService.sendEvent(null, NotificationEventType.RE_INVITE_USER, emptyMap())).then {  }
        // Invite user
        mockMvc.post("/admin/user-context/invite") {
            param("email", "test@gmail.com")
            param("name", "firstname")
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }

        val context = onboardingContextRepository.findByPersonalEmail("test@gmail.com")
        context!!.onboardingContextStatus = OnboardingContextStatus.TO_BE_VERIFIED
        onboardingContextRepository.save(context)

        // Re-invite user
        mockMvc.post("/admin/user-context/invite") {
            param("email", "test@gmail.com")
            param("name", "firstname")
            param("comment", "comment")
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }
    }

    @Test
    fun shouldNotBeInvitedByUnauthorizedUser() {
        val authUser = AuthUser(
            username = "Test@Test",
            password = "test",
            status = AuthUserStatus.ENABLED,
            role = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        )
        val employee = userRepository.save(
            User(
                firstName = "test",
                lastName = "test",
                employeeCode = "testcode",
                designation = "designation",
                authUser = authUser,
                userDetails = null,
                reporter = null
            )
        )

        val employeeUser = userRepository.findByFirstName(employee.firstName)!!
        val token = "Bearer ${JwtService.generateToken(employeeUser.authUser)}"
        mockMvc.post("/admin/user-context/invite") {
            param("email", "test123")
            param("name", "firstname")
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun shouldHaveStatusNotFound() {
        val email = "test1"
        val inviteCode = "testcode1"
        val invalidEmail = "invalid email"
        onboardingContextRepository.save(
            OnboardedContextGenerator.getOnBoardedContext(personalEmail = email, inviteCode = inviteCode)
        )
        mockMvc.get("/employee/user-context/validate-code") {
            param("email", invalidEmail)
            param("inviteCode", inviteCode)
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", "") }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun shouldValidateInviteCode() {
        val email = "test2"
        val inviteCode = "testcode2"
        onboardingContextRepository.save(
            OnboardedContextGenerator.getOnBoardedContext(personalEmail = email, inviteCode = inviteCode)
        )
        Mockito.`when`(mockOnboardingContextService.generateInviteCode()).thenReturn(inviteCode)
        mockMvc.get("/employee/user-context/validate-code") {
            param("email", email)
            param("inviteCode", inviteCode)
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", "") }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }
    }

    @Test
    fun shouldHaveStatusNotAcceptable() {
        val email = "test3"
        val inviteCode = "testcode3"
        val invalidInviteCode = "invalidCode"
        onboardingContextRepository.save(
            OnboardedContextGenerator.getOnBoardedContext(personalEmail = email, inviteCode = inviteCode)
        )
        Mockito.`when`(mockOnboardingContextService.generateInviteCode()).thenReturn(inviteCode)
        mockMvc.get("/employee/user-context/validate-code") {
            param("email", email)
            param("inviteCode", invalidInviteCode)
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", "") }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotAcceptable() }
        }
    }

    @Test
    fun shouldSaveUserBasicDetails() {
        val context = onboardingContextService.saveInviteDetails("test@gmail.com", "firstname")
        val basicDetailsRequest = BasicDetailsRequestGenerator.getBasicDetailsRequest()
        basicDetailsRequest.id = context.id
        mockMvc.post("/employee/user-context/update") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(basicDetailsRequest)
            headers { header(name = "Authorization", "") }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun shouldUpdateOnboardingContextStatus() {
        val context = onboardingContextService.saveInviteDetails("test@gmail.com", "firstname")
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        mockMvc.put("/admin/user-context/update-status") {
            param("onboardingContextId", "${context.id}")
            param("status", OnboardingContextStatus.APPROVED.name)
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    fun shouldGetAllOnboardingContexts() {
        for (i in 1..15) {
            onboardingContextRepository.save(
                OnboardedContextGenerator.getOnBoardedContext(personalEmail = "user${i}@gmail.com")
            )
        }
        val adminRole = authRoleRepository.findById(1).get()
        val user = userRepository.save(
            User(
                firstName = "testing",
                lastName = "testing",
                employeeCode = "BNK1",
                designation = "designation",
                authUser = AuthUser(
                    username = "pageUser",
                    password = passwordEncoder.encode("test"),
                    status = AuthUserStatus.ENABLED,
                    role = adminRole
                ),
                userDetails = null,
                reporter = null
            )
        )

        val token = "Bearer ${JwtService.generateToken(user.authUser)}"
        val responses: PageResponse<OnboardingContextResponse>
        val result = mockMvc.get("/admin/user-context") {
            contentType = MediaType.APPLICATION_JSON
            param("pageNumber", "0")
            param("pageSize", "5")
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andReturn()
        responses = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<PageResponse<OnboardingContextResponse>?>() {}
        )!!

        Assertions.assertEquals(5, responses.contents.size)
        Assertions.assertEquals(true, responses.totalCount >= 15)
    }

    @Test
    fun shouldGetOnboardingContext() {
        val onboardingContext = onboardingContextRepository.save(
            OnboardedContextGenerator.getOnBoardedContext(personalEmail = "user123@gmail.com")
        )

        val result = mockMvc.get("/admin/user-context/view") {
            contentType = MediaType.APPLICATION_JSON
            param("inviteCode", onboardingContext.inviteCode)
            param("email", onboardingContext.personalEmail)
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()
        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            BasicDetailsResponse::class.java
        )!!

        Assertions.assertEquals(onboardingContext.personalEmail, response.personalEmail)
        Assertions.assertEquals(onboardingContext.aadhaarNumber, response.aadhaarNumber)
        Assertions.assertEquals(onboardingContext.panNumber, response.panNumber)
        Assertions.assertEquals(onboardingContext.contactNumber, response.contactNumber)
        Assertions.assertEquals(onboardingContext.onboardingContextStatus, response.onboardingContextStatus)
    }
}