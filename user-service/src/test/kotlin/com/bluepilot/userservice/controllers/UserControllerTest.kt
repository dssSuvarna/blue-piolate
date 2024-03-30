package com.bluepilot.userservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.entities.BankDetails
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.Role
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import com.bluepilot.userservice.generators.UpdateESIAndPFDetailsRequestGenerator
import com.bluepilot.userservice.generators.UpdateUserDetailsRequestGenerator
import com.bluepilot.userservice.models.responses.EmployeeSummary
import com.bluepilot.userservice.models.responses.TrainingDetailsResponse
import com.bluepilot.userservice.models.responses.UserAcademicDetailsResponse
import com.bluepilot.userservice.models.responses.UserBankDetailsResponse
import com.bluepilot.userservice.models.responses.UserESIAndPFDetailsResponse
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
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class UserControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val userGenerator: UserGenerator,
    val roleRepository: AuthRoleRepository
) : BaseTestConfig() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var eventService: EventService

    @Test
    fun shouldGetUserByUserId() {
        val savedUser = userRepository.save(userGenerator.getUser(userName = "usernametest1"))
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        mockMvc.get("/employee/user/${savedUser.id}") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }
    }

    @Test
    fun shouldUpdateUserDetailsByUser() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user = userRepository.save(
            userGenerator.getUser(
                userName = "username2",
                authRole = employeeRole!!
            )
        )
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"
        mockMvc.put("/employee/user/details/update") {
            contentType = MediaType.APPLICATION_JSON
            content =
                ObjectMapper().writeValueAsString(
                    UpdateUserDetailsRequestGenerator.getUpdateBasicDetailsRequest(
                        firstName = "ChangedName"
                    )
                )
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun shouldNotUpdateUserDetailsByUnauthorizedRole() {
        //Admin cannot update basic details of any particular user
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        userRepository.save(userGenerator.getUser(userName = "username3"))
        mockMvc.put("/employee/user/details/update") {
            contentType = MediaType.APPLICATION_JSON
            content =
                ObjectMapper().writeValueAsString(
                    UpdateUserDetailsRequestGenerator.getUpdateBasicDetailsRequest(
                        firstName = "ChangedName"
                    )
                )
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun shouldGetBankDetailsWithAdminUser() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user = userGenerator.getUser(
            withBankDetails = BankDetails(
                accountNumber = 12365498778L,
                ifsc = "IFSCCODE012",
                bankName = "TestBank",
                accountHolderName = "account holder name"
            ),
            userName = "bankusername1",
            authRole = employeeRole!!
        )
        val savedUser = userRepository.save(user)
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val result = mockMvc.get("/admin/user/bank-details/${savedUser.id}") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            UserBankDetailsResponse::class.java
        )!!

        Assertions.assertEquals(user.userDetails!!.bankDetails!!.accountNumber, response.accountNumber)
        Assertions.assertEquals(user.userDetails!!.bankDetails!!.ifsc, response.ifsc)
        Assertions.assertEquals(user.userDetails!!.bankDetails!!.bankName, response.bankName)
    }

    @Test
    fun shouldGetBankDetails() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user = userGenerator.getUser(
            withBankDetails = BankDetails(
                accountNumber = 12365498778L,
                ifsc = "IFSCCODE012",
                bankName = "TestBank",
                accountHolderName = "account holder name"
            ),
            userName = "bankusername2",
            authRole = employeeRole!!
        )
        val savedUser = userRepository.save(user)
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"
        val result = mockMvc.get("/employee/user/bank-details/${savedUser.id}") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            UserBankDetailsResponse::class.java
        )!!

        Assertions.assertEquals(user.userDetails!!.bankDetails!!.accountNumber, response.accountNumber)
        Assertions.assertEquals(user.userDetails!!.bankDetails!!.ifsc, response.ifsc)
        Assertions.assertEquals(user.userDetails!!.bankDetails!!.bankName, response.bankName)
    }

    @Test
    fun shouldThrowExceptionWhenUnauthorizedUserTryToGetBankDetails() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user1 = userGenerator.getUser(
            withBankDetails = BankDetails(
                accountNumber = 12365498778L,
                ifsc = "IFSCCODE012",
                bankName = "TestBank",
                accountHolderName = "account holder name"
            ),
            userName = "bankusername3"
        )
        val savedUser1 = userRepository.save(user1)

        val user2 = userGenerator.getUser(
            withBankDetails = BankDetails(
                accountNumber = 12365498778L,
                ifsc = "IFSCCODE012",
                bankName = "TestBank",
                accountHolderName = "account holder name"
            ),
            userName = "bankusername4",
            authRole = employeeRole!!
        )
        userRepository.save(user2)

        val token = "Bearer ${JwtService.generateToken(user2.authUser)}"
        mockMvc.get("/employee/user/bank-details/${savedUser1.id}") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isUnauthorized() } }.andReturn()
    }

    @Test
    fun shouldGetAcademicDetailsWithAdminUser() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user = userGenerator.getUser(authRole = employeeRole!!)
        val savedUser = userRepository.save(user)
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val result = mockMvc.get("/admin/user/academic-details/${savedUser.id}") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            UserAcademicDetailsResponse::class.java
        )!!

        Assertions.assertEquals(user.userDetails!!.academicDetails.tenthPercentage.setScale(2), response.tenthPercentage)
        Assertions.assertEquals(user.userDetails!!.academicDetails.tenthPassOutYear, response.tenthPassOutYear)
        Assertions.assertEquals(user.userDetails!!.academicDetails.twelfthPercentage.setScale(2), response.twelfthPercentage)
        Assertions.assertEquals(user.userDetails!!.academicDetails.twelfthPassOutYear, response.twelfthPassOutYear)
        Assertions.assertEquals(user.userDetails!!.academicDetails.twelfthCourse, response.twelfthCourse)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degree, response.degree)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degreePassOutYear, response.degreePassOutYear)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degreeCourse, response.degreeCourse)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degreePercentage.setScale(2), response.degreePercentage)
        Assertions.assertEquals(user.userDetails!!.academicDetails.document, response.document)
        Assertions.assertEquals(user.userDetails!!.academicDetails.tenthInstitute, response.tenthInstitute)
        Assertions.assertEquals(user.userDetails!!.academicDetails.twelfthInstitute, response.twelfthInstitute)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degreeInstitute, response.degreeInstitute)
    }

    @Test
    fun shouldGetAcademicDetails() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user = userGenerator.getUser(userName = "academicuser2", authRole = employeeRole!!)
        val savedUser = userRepository.save(user)
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"
        val result = mockMvc.get("/employee/user/academic-details/${savedUser.id}") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            UserAcademicDetailsResponse::class.java
        )!!

        Assertions.assertEquals(user.userDetails!!.academicDetails.tenthPercentage.setScale(2), response.tenthPercentage)
        Assertions.assertEquals(user.userDetails!!.academicDetails.tenthPassOutYear, response.tenthPassOutYear)
        Assertions.assertEquals(user.userDetails!!.academicDetails.twelfthPercentage.setScale(2), response.twelfthPercentage)
        Assertions.assertEquals(user.userDetails!!.academicDetails.twelfthPassOutYear, response.twelfthPassOutYear)
        Assertions.assertEquals(user.userDetails!!.academicDetails.twelfthCourse, response.twelfthCourse)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degree, response.degree)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degreePassOutYear, response.degreePassOutYear)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degreeCourse, response.degreeCourse)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degreePercentage.setScale(2), response.degreePercentage)
        Assertions.assertEquals(user.userDetails!!.academicDetails.document, response.document)
    }

    @Test
    fun shouldThrowExceptionWhenUnauthorizedUserTryToGetAcademicDetails() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user1 = userGenerator.getUser(userName = "academicuser3")
        val savedUser1 = userRepository.save(user1)

        val user2 = userGenerator.getUser(userName = "academicuser4", authRole = employeeRole!!)
        userRepository.save(user2)

        val token = "Bearer ${JwtService.generateToken(user2.authUser)}"
        mockMvc.get("/employee/user/academic-details/${savedUser1.id}") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isUnauthorized() } }.andReturn()
    }

    @Test
    fun shouldAddESIAndPFDetailsByUser() {
        Mockito.`when`(eventService.sendEvent(null, NotificationEventType.ESI_AND_PF_UPDATE, emptyMap())).then {  }
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val employee = userGenerator.getUser(userName = "username4", authRole = employeeRole)
        val savedUser = userRepository.save(employee)
        val token = "Bearer ${JwtService.generateToken(savedUser.authUser)}"

        val updateESIAndPFDetailsByUserRequest =
            UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByUserRequest(userId = savedUser.id)
        val result = mockMvc.post("/employee/user/esi-pf-details/update") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(updateESIAndPFDetailsByUserRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            UserESIAndPFDetailsResponse::class.java
        )!!

        Assertions.assertNotNull(response)
        Assertions.assertEquals(updateESIAndPFDetailsByUserRequest.adhaarName, response.adhaarName)
    }

    @Test
    fun shouldNotAddESIAndPFByUnauthorizedRole() {
        //Admin or HR can only update ESI and PF details, but not allowed to add
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val employee = userGenerator.getUser(userName = "username5", authRole = employeeRole)
        val savedUser = userRepository.save(employee)

        mockMvc.post("/employee/user/esi-pf-details/update") {
            contentType = MediaType.APPLICATION_JSON
            content =
                ObjectMapper().writeValueAsString(
                    UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByUserRequest(userId = savedUser.id)
                )
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun shouldUpdateESIAndPFDetailsByHROrAdmin() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val employee = userGenerator.getUser(userName = "username6", authRole = employeeRole)
        val savedUser = userRepository.save(employee)

        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val updateESIAndPFDetailsByHRRequest =
            UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByHRRequest(userId = savedUser.id)
        val result = mockMvc.post("/admin/user/esi-pf-details/update") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(updateESIAndPFDetailsByHRRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            UserESIAndPFDetailsResponse::class.java
        )!!

        Assertions.assertNotNull(response)
        Assertions.assertEquals(updateESIAndPFDetailsByHRRequest.uanNo, response.uanNo)
    }

    @Test
    fun shouldNotUpdateESIAndPFDetailsByEmployee() {
        //Employee can only add basic ESI and PF details, Only HR/Admin are allowed to update rest of the details
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val employee = userGenerator.getUser(userName = "username7", authRole = employeeRole)
        val savedUser = userRepository.save(employee)

        val token = "Bearer ${JwtService.generateToken(savedUser.authUser)}"
        mockMvc.post("/admin/user/esi-pf-details/update") {
            contentType = MediaType.APPLICATION_JSON
            content =
                ObjectMapper().writeValueAsString(
                    UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByHRRequest(userId = savedUser.id)
                )
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun shouldFetchESIAndPFDetailsForUserByToken() {
        //Details can only be fetched by HR or Admin and User bearing same userId
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val employee = userGenerator.getUser(userName = "username8", authRole = employeeRole)
        val savedUser = userRepository.save(employee)

        val token = "Bearer ${JwtService.generateToken(savedUser.authUser)}"
        mockMvc.get("/employee/user/esi-pf-details") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }
    }

    @Test
    fun shouldFetchESIAndPFDetailsForAdminById() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val employee = userGenerator.getUser(userName = "username9", authRole = employeeRole)
        val savedUser = userRepository.save(employee)
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        mockMvc.get("/admin/user/esi-pf-details/${savedUser.id}") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }
    }


    @Test
    fun getAllEmployeeSummary() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val savedUser = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val result = mockMvc.get("/admin/user/summary") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<List<EmployeeSummary>>() {}
        )!!

        Assertions.assertEquals(response.size, 3)
        Assertions.assertNotNull(response.firstOrNull { it.employeeCode == savedUser.employeeCode })
    }
}