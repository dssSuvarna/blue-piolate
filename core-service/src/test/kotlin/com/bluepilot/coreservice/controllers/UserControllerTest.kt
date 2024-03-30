package com.bluepilot.coreservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.coreservice.generators.OnboardedContextGenerator
import com.bluepilot.coreservice.generators.SystemResourceRequestGenerator
import com.bluepilot.coreservice.models.requests.CreateBankDetailsRequest
import com.bluepilot.coreservice.models.requests.CreateUserRequest
import com.bluepilot.coreservice.models.requests.UnAssignUserSystemResourceRequest
import com.bluepilot.coreservice.models.requests.UpdatePasswordRequest
import com.bluepilot.coreservice.models.requests.UpdateUserResourceRequest
import com.bluepilot.coreservice.models.requests.UserRequestFilter
import com.bluepilot.coreservice.models.responses.UserResourceResponse
import com.bluepilot.coreservice.models.responses.UserResponse
import com.bluepilot.coreservice.models.responses.UserUnAssignSystemResourceResponse
import com.bluepilot.coreservice.services.S3Service
import com.bluepilot.coreservice.services.SystemResourceService
import com.bluepilot.coreservice.services.UserService
import com.bluepilot.entities.AuthUser
import com.bluepilot.entities.User
import com.bluepilot.entities.UserResource
import com.bluepilot.enums.AuthUserStatus
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.OnboardingContextStatus
import com.bluepilot.enums.Role
import com.bluepilot.enums.SystemResourceStatus
import com.bluepilot.enums.UserStatus
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthUserRepository
import com.bluepilot.repositories.OnboardingContextRepository
import com.bluepilot.repositories.RoleRepository
import com.bluepilot.repositories.SystemResourcesRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.utils.Mockito.Companion.anyObject
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.nio.file.Files
import java.sql.Date
import java.time.Instant
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.name


@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class UserControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val passwordEncoder: PasswordEncoder,
    val authUserRepository: AuthUserRepository,
    val onboardingContextRepository: OnboardingContextRepository,
    val userGenerator: UserGenerator,
    val systemResourceService: SystemResourceService,
    val systemResourcesRepository: SystemResourcesRepository,
    val userService: UserService
) : BaseTestConfig() {

    @MockBean
    lateinit var s3Service: S3Service

    @MockBean
    lateinit var eventService: EventService

    @Autowired
    lateinit var mockMvc: MockMvc


    @BeforeEach
    fun saveOnboardingContext() {
        `when`(s3Service.moveFileToDifferentFolder(Mockito.anyString(), Mockito.anyString())).thenReturn("")
        `when`(eventService.processEvent(anyObject())).then {  }
    }

    @Test
    fun shouldChangePassword() {
        val role = roleRepository.findById(1).get()
        val body = UpdatePasswordRequest(
            "testUser",
            "Test@123454"
        )

        val user = User(
            firstName = "testing",
            lastName = "testing",
            employeeCode = "t012",
            designation = "designation",
            authUser = AuthUser(
                username = "test@gmail.com",
                password = passwordEncoder.encode(body.oldPassword),
                status = AuthUserStatus.ENABLED,
                role = role
            ),
            userDetails = null,
            reporter = null
        )

        userRepository.save(user)

        passwordEncoder.matches(body.oldPassword, user.authUser.password)
        //Before password changed
        Assertions.assertEquals(true, passwordEncoder.matches(body.oldPassword, user.authUser.password))

        val token = "Bearer ${JwtService.generateToken(user.authUser)}"
        `when`(eventService.sendEvent(null, NotificationEventType.RE_INVITE_USER, emptyMap())).then {  }

        mockMvc.put("/employee/user/update-password") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(body)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }

        val authUser = authUserRepository.findByUsername(username = user.authUser.username)

        //After password changed
        Assertions.assertEquals(true, passwordEncoder.matches(body.newPassword, authUser!!.password))

    }

    @Test
    fun shouldRegisterNewUser() {
        val onboardedContext = onboardingContextRepository.save(
           OnboardedContextGenerator.getOnBoardedContext(onboardingContextStatus = OnboardingContextStatus.APPROVED)
       )

        val adminRole = roleRepository.findById(1).get()
        val user = userRepository.save(
            User(
                firstName = "testing",
                lastName = "testing",
                employeeCode = "t012",
                designation = "designation",
                authUser = AuthUser(
                    username = "testUser2",
                    password = passwordEncoder.encode("test"),
                    status = AuthUserStatus.ENABLED,
                    role = adminRole
                ),
                userDetails = null,
                reporter = null
            )
        )

        val body = CreateUserRequest(
            onboardingContextId = onboardedContext.id,
            role = Role.EMPLOYEE,
            designation = "designation",
            dateOfJoining = Date(Instant.now().toEpochMilli()),
            reporterId = user.id,
            professionalEmail = "professional@gmail.com"
        )
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

        mockMvc.post("/admin/user/register") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(body)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun shouldUpdateUserBankDetails() {

        val onboardedContext = onboardingContextRepository.save(
            OnboardedContextGenerator.getOnBoardedContext(
                personalEmail = "bankdetails@gmail.com", onboardingContextStatus = OnboardingContextStatus.APPROVED
            )
        )
        val adminRole = roleRepository.findById(1).get()
        val user = userRepository.save(
            User(
                firstName = "testing",
                lastName = "testing",
                employeeCode = "BNK1",
                designation = "designation",
                authUser = AuthUser(
                    username = "bankadder",
                    password = passwordEncoder.encode("test"),
                    status = AuthUserStatus.ENABLED,
                    role = adminRole
                ),
                userDetails = null,
                reporter = null
            )
        )

        val createUserRequest = CreateUserRequest(
            onboardingContextId = onboardedContext.id,
            role = Role.EMPLOYEE,
            designation = "designation",
            dateOfJoining = Date(Instant.now().toEpochMilli()),
            reporterId = user.id,
            professionalEmail = "professional@gmail.com"
        )
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

     val result = mockMvc.post("/admin/user/register") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(createUserRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        val response = ObjectMapper().readValue(result.response.contentAsString, UserResponse::class.java)
        val createBankDetailsRequestBody = CreateBankDetailsRequest(
            accountNumber = 12345678910L,
            ifsc = "SCCCVV",
            bankName = "TestingBank",
            accountHolderName = "account holder name"
        )
       val savedEmployee = userRepository.findByEmployeeCode(response.employeeCode)

        mockMvc.post("/admin/user/bank-details/${savedEmployee.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(createBankDetailsRequestBody)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect{status { isOk() }}
    }

    @Test
    fun shouldNotUpdateUserBankDetailsWhenUserUnauthorized() {
        val employeeRole = roleRepository.findById(3).get()
        val user = userRepository.save(
            User(
                firstName = "testing",
                lastName = "testing",
                employeeCode = "BNK1",
                designation = "designation",
                authUser = AuthUser(
                    username = "bankadder1",
                    password = passwordEncoder.encode("test"),
                    status = AuthUserStatus.ENABLED,
                    role = employeeRole
                ),
                userDetails = null,
                reporter = null
            )
        )

        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

        val createBankDetailsRequestBody = CreateBankDetailsRequest(
            accountNumber = 12345678910L,
            ifsc = "SCCCVV",
            bankName = "TestingBank",
            accountHolderName = "account holder name"
        )

        mockMvc.post("/admin/user/bank-details/100001") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(createBankDetailsRequestBody)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isInternalServerError() } }
    }

    @Test
    fun shouldGetPaginatedUsers() {
        val adminUser = userRepository.findAll().first { it.authUser.role.name == Role.ADMIN }
        val createUsersReq: MutableList<CreateUserRequest> = mutableListOf()
        for (i in 1..10) {
            val savedOnboardingContext = onboardingContextRepository.save(
                OnboardedContextGenerator.getOnBoardedContext(
                    personalEmail = "user${i}@gmail.com", onboardingContextStatus = OnboardingContextStatus.APPROVED
                )
            )

            createUsersReq.add(
                CreateUserRequest(
                    onboardingContextId = savedOnboardingContext.id,
                    role = Role.EMPLOYEE,
                    designation = "designation",
                    dateOfJoining = Date(Instant.now().toEpochMilli()),
                    reporterId = adminUser.id,
                    professionalEmail = "professional${i}@gmail.com"
                )
            )
        }

        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"

        for (createUserRequestBody in createUsersReq) {
            mockMvc.post("/admin/user/register") {
                contentType = MediaType.APPLICATION_JSON
                content = ObjectMapper().writeValueAsString(createUserRequestBody)
                headers { header(name = "Authorization", token) }
                accept = MediaType.APPLICATION_JSON
            }
        }
        val users = userRepository.findAll()
        for (i in 2..4) {
            users[i].status = UserStatus.ACTIVE
        }
        userRepository.saveAll(users)

        var getUserReq = UserRequestFilter(
            status = UserStatus.DEACTIVE
        )

        var result = mockMvc.post("/admin/user") {
            contentType = MediaType.APPLICATION_JSON
            param("pageNumber", "0")
            param("pageSize", "5")
            content = ObjectMapper().writeValueAsString(getUserReq)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        var response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<PageResponse<UserResponse>?>() {}
        )!!
        Assertions.assertEquals(0, response.contents.size)
        Assertions.assertEquals(true, response.totalCount >= 0L)

        getUserReq = UserRequestFilter(
            status = UserStatus.ACTIVE
        )

        result = mockMvc.post("/admin/user") {
            contentType = MediaType.APPLICATION_JSON
            param("pageNumber", "0")
            param("pageSize", "5")
            content = ObjectMapper().writeValueAsString(getUserReq)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<PageResponse<UserResponse>?>() {}
        )!!
        Assertions.assertEquals(3, response.contents.size)
        Assertions.assertEquals(true, response.totalCount == 3L)
    }

    @Test
    fun shouldUpdateUserStatus() {
        val employeeRole = roleRepository.findById(1).get()
        val adminUser = authUserRepository.findById(1).get()
        val user = userRepository.save(
            User(
                firstName = "testing",
                lastName = "testing",
                employeeCode = "BNK1",
                designation = "designation",
                status = UserStatus.ONBOARDED,
                authUser = AuthUser(
                    username = "statusUser2",
                    password = passwordEncoder.encode("test"),
                    status = AuthUserStatus.ENABLED,
                    role = employeeRole
                ),
                userDetails = null,
                reporter = null
            )
        )

        val token = "Bearer ${JwtService.generateToken(adminUser)}"
        mockMvc.put("/admin/user/status") {
            contentType = MediaType.APPLICATION_JSON
            param("userId", "${user.id}")
            param("status", "${UserStatus.ACTIVE}")
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        var savedUser = userRepository.findById(user.id)
        Assertions.assertEquals(UserStatus.ACTIVE, savedUser.get().status)

        mockMvc.put("/admin/user/status") {
            contentType = MediaType.APPLICATION_JSON
            param("userId", "${user.id}")
            param("status", "${UserStatus.DEACTIVE}")
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        savedUser = userRepository.findById(user.id)
        Assertions.assertEquals(UserStatus.DEACTIVE, savedUser.get().status)
    }

    @Test
    fun shouldThrowExceptionWhenUserStatusInvalidTransition() {
        val employeeRole = roleRepository.findById(1).get()
        val adminUser = authUserRepository.findById(1).get()
        val user = userRepository.save(
            User(
                firstName = "testing",
                lastName = "testing",
                employeeCode = "BNK2",
                designation = "designation",
                status = UserStatus.CREATED,
                authUser = AuthUser(
                    username = "statusUser2",
                    password = passwordEncoder.encode("test"),
                    status = AuthUserStatus.ENABLED,
                    role = employeeRole
                ),
                userDetails = null,
                reporter = null
            )
        )

        val token = "Bearer ${JwtService.generateToken(adminUser)}"
        mockMvc.put("/admin/user/status") {
            contentType = MediaType.APPLICATION_JSON
            param("userId", "${user.id}")
            param("status", "${UserStatus.ACTIVE}")
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isNotAcceptable() } }
    }

    @Test
    fun shouldAddUserResourceForUser() {
        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"
        val user = userGenerator.getUser()
        userRepository.save(user)
        val systemResourceRequest =
            SystemResourceRequestGenerator.getAddSystemResourceRequest(systemId = "systemId10")
        val systemResource = systemResourceService.addSystemResources(systemResourceRequest)
        val updateUserResource = UpdateUserResourceRequest(
            userId = user.id,
            systemResourceId = systemResource.id,
            idCard = "id-value",
            professionalEmail = "prof@gmail.com"
        )
        val result = mockMvc.post("/admin/user/update/resource") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(updateUserResource)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<UserResourceResponse>() {}
        )!!
        Assertions.assertEquals(response.systemResourcesResponse!!.systemId, systemResource.systemId)
        Assertions.assertEquals(response.idCard, updateUserResource.idCard)
        Assertions.assertEquals(response.professionalEmail, updateUserResource.professionalEmail)
    }

    @Test
    fun shouldUpdateUserResourceForUser() {
        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"
        var user = userGenerator.getUser()
        userRepository.save(user)
        val systemResourceRequest =
            SystemResourceRequestGenerator.getAddSystemResourceRequest(systemId = "systemId1")
        val systemResource = systemResourceService.addSystemResources(systemResourceRequest)
        val createUserResource = UpdateUserResourceRequest(
            userId = user.id,
            systemResourceId = systemResource.id,
            idCard = "id-value",
            professionalEmail = "prof@gmail.com"
        )
        mockMvc.post("/admin/user/update/resource") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(createUserResource)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val updatedSystemRequest =
            SystemResourceRequestGenerator.getAddSystemResourceRequest(systemId = "systemId2")
        val updatedSystemResource = systemResourceService.addSystemResources(updatedSystemRequest)

        val updateUserResource = UpdateUserResourceRequest(
            userId = user.id,
            systemResourceId = updatedSystemResource.id,
            idCard = "id-value-updated",
            professionalEmail = "updated@gmail.com"
        )
        val result = mockMvc.post("/admin/user/update/resource") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(updateUserResource)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<UserResourceResponse>() {}
        )!!

        val foundUpdatedSystemResource = systemResourceService.getSystemResourceById(updatedSystemResource.id)
        user = userRepository.findById(user.id).get()
        Assertions.assertEquals(user.userDetails!!.professionalEmail, updateUserResource.professionalEmail)
        Assertions.assertEquals(user.authUser.username, updateUserResource.professionalEmail)
        Assertions.assertEquals(response.idCard, updateUserResource.idCard)
        Assertions.assertEquals(response.professionalEmail, updateUserResource.professionalEmail)
        Assertions.assertEquals(response.systemResourcesResponse!!.id, foundUpdatedSystemResource.id)
        Assertions.assertEquals(response.systemResourcesResponse!!.systemId, foundUpdatedSystemResource.systemId)
    }

    @Test
    fun shouldUnAssignedSystemResourceFromUser() {
        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"
        var user = userGenerator.getUser()
        userRepository.save(user)
        val systemResourceRequest =
            SystemResourceRequestGenerator.getAddSystemResourceRequest(systemId = "systemId1")
        val systemResource = systemResourceService.addSystemResources(systemResourceRequest)
        val createUserResource = UpdateUserResourceRequest(
            userId = user.id,
            systemResourceId = systemResource.id,
            idCard = "id-value",
            professionalEmail = "prof@gmail.com"
        )
        mockMvc.post("/admin/user/update/resource") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(createUserResource)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        user = userRepository.findById(user.id).get()
        val unAssignUserSystemResourceRequest = UnAssignUserSystemResourceRequest(
            userId = user.id,
            systemResourceId = systemResource.id
        )
        val assignedSystem = systemResourcesRepository.findById(systemResource.id).get()

        //when system is assigned
        Assertions.assertEquals(assignedSystem.status, SystemResourceStatus.ASSIGNED)
        Assertions.assertEquals(user.resource!!.systemResource, assignedSystem)


        val result = mockMvc.post("/admin/user/un-assign/system-resource") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(unAssignUserSystemResourceRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        user = userRepository.findById(user.id).get()
        val unAssignedSystem = systemResourcesRepository.findById(systemResource.id).get()

        //when system is unAssigned
        Assertions.assertEquals(unAssignedSystem.status, SystemResourceStatus.UNASSIGNED)
        Assertions.assertEquals(user.resource!!.systemResource, null)

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<UserUnAssignSystemResourceResponse>() {}
        )!!

        Assertions.assertEquals(systemResource.id, response.id)
        Assertions.assertEquals(systemResource.systemId, response.systemId)
        Assertions.assertEquals(
            "System successfully unassigned from ${user.firstName} ${user.lastName}",
            response.message
        )

        val newSystemResourceRequest =
            SystemResourceRequestGenerator.getAddSystemResourceRequest(systemId = "systemId20")
        val newSystemResource = systemResourceService.addSystemResources(newSystemResourceRequest)
        val newUpdateUserResource = UpdateUserResourceRequest(
            userId = user.id,
            systemResourceId = newSystemResource.id,
            idCard = "id-value",
            professionalEmail = "prof@gmail.com"
        )

        val newresult = mockMvc.post("/admin/user/update/resource") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(newUpdateUserResource)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        Assertions.assertEquals(true,true)
    }

    @Test
    fun shouldFetchUserResourceByUserId() {
        val systemResourceRequest =
            SystemResourceRequestGenerator.getAddSystemResourceRequest()
        val systemResource = systemResourceService.addSystemResources(systemResourceRequest)
        val employeeRole = roleRepository.findById(1).get()
        val user = userGenerator.getUser(authRole = employeeRole)
        var savedUser = userRepository.save(user)
        user.resource = UserResource(
            idCard = "ID_CARD",
            professionalEmail = "email@123",
            systemResource = systemResourcesRepository.findBySystemId(systemResource.systemId)!!,
            userId = savedUser.id
        )
        savedUser = userRepository.save(user)

        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"
        val result = mockMvc.get("/admin/user/resource/${savedUser.id}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<UserResourceResponse>() {}
        )!!

        Assertions.assertEquals(savedUser.resource!!.id, response.id)
        Assertions.assertEquals(savedUser.resource!!.idCard, response.idCard)
        Assertions.assertEquals(savedUser.resource!!.professionalEmail, response.professionalEmail)
        Assertions.assertEquals(savedUser.resource!!.systemResource!!.id, response.systemResourcesResponse!!.id)
        Assertions.assertEquals(
            savedUser.resource!!.systemResource!!.systemId,
            response.systemResourcesResponse!!.systemId
        )
    }

    @Test
    fun shouldUpdateProfilePicture() {
        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"

        val file = Files.createTempFile("text.txt", "")
        val multipartFile =
            MockMultipartFile("file", file.name, MediaType.MULTIPART_FORM_DATA_VALUE, file.inputStream())

        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/admin/user/profile-picture")
                .file(multipartFile)
                .header("Authorization", token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
        file.deleteIfExists()
    }

    @Test
    fun shouldUpdateWeekOffForUser() {
        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"

        val employeeRole = roleRepository.findById(1).get()
        var user = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        //By default, saturday is not a week-off for a user
        Assertions.assertFalse(user.userDetails!!.saturdayOff)

        //Updating user to have a week-off on saturday
        mockMvc.put("/admin/user/saturday-off") {
            contentType = MediaType.APPLICATION_JSON
            param("userId", "${user.id}")
            param("saturdayOff", "true")
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        user = userService.getUserById(user.id)
        Assertions.assertTrue(user.userDetails!!.saturdayOff)
    }
}