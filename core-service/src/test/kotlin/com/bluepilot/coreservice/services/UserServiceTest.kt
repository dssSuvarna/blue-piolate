package com.bluepilot.coreservice.services

import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.coreservice.generators.OnboardedContextGenerator
import com.bluepilot.coreservice.models.requests.CreateBankDetailsRequest
import com.bluepilot.coreservice.models.requests.CreateUserRequest
import com.bluepilot.coreservice.models.requests.UserRequestFilter
import com.bluepilot.entities.OnboardingContext
import com.bluepilot.enums.OnboardingContextStatus
import com.bluepilot.enums.Role
import com.bluepilot.enums.UserStatus
import com.bluepilot.errors.ErrorMessages.Companion.RESOURCE_NOT_FOUND
import com.bluepilot.errors.ErrorMessages.Companion.USER_NOT_FOUND
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.OnboardingContextRepository
import com.bluepilot.repositories.RoleRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.utils.Mockito.Companion.anyObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.math.RoundingMode
import java.sql.Date
import java.time.Instant

@SpringBootTest
class UserServiceTest @Autowired constructor(
    val onboardingContextRepository: OnboardingContextRepository,
    val userRepository: UserRepository,
    val userGenerator: UserGenerator,
    val roleRepository: RoleRepository
) : BaseTestConfig() {

    lateinit var onboardingContext: OnboardingContext

    @MockBean
    lateinit var s3Service: S3Service

    @MockBean
    lateinit var eventService: EventService


    @Autowired
    lateinit var userService: UserService

    @BeforeEach
    fun saveOnboardingContext() {
        onboardingContext = onboardingContextRepository.save(
            OnboardedContextGenerator.getOnBoardedContext(
                onboardingContextStatus = OnboardingContextStatus.APPROVED
            )
        )
        `when`(s3Service.moveFileToDifferentFolder(anyString(), anyString())).thenReturn("")
        `when`(eventService.processEvent(anyObject())).then {  }
    }

    @Test
    fun shouldRegisterNewUserFromOnboardingContext() {
        val createUserRequest = CreateUserRequest(
            onboardingContextId = onboardingContext.id,
            role = Role.EMPLOYEE,
            designation = "designation",
            dateOfJoining = Date(Instant.now().toEpochMilli()),
            reporterId = 1,
            professionalEmail = "professional@gmail.com"
        )
        val response = userService.registerUser(createUserRequest)
        val user = userRepository.findByEmployeeCode(response.employeeCode)
        Assertions.assertEquals(user.firstName, onboardingContext.firstName)
        Assertions.assertEquals(user.lastName, "${onboardingContext.middleName ?: ""} ${onboardingContext.lastName!!}")
        Assertions.assertEquals(user.userDetails!!.adhaarNumber, onboardingContext.aadhaarNumber)
        Assertions.assertEquals(user.userDetails!!.adhaarDoc, "")
        Assertions.assertEquals(user.userDetails!!.contactNumber, onboardingContext.contactNumber)
        Assertions.assertEquals(user.userDetails!!.alternateContactNumber, onboardingContext.alternateContactNumber)
        Assertions.assertEquals(user.userDetails!!.dateOfBirth, onboardingContext.dateOfBirth)
        Assertions.assertEquals(user.userDetails!!.dateOfJoining.toLocalDate(), createUserRequest.dateOfJoining.toLocalDate())
        Assertions.assertEquals(user.userDetails!!.academicDetails.tenthPassOutYear, onboardingContext.tenthPassoutYear)
        Assertions.assertEquals(user.userDetails!!.academicDetails.tenthPercentage, onboardingContext.tenthPercentage!!.setScale(2))
        Assertions.assertEquals(user.userDetails!!.academicDetails.tenthInstitute, onboardingContext.tenthInstitute)
        Assertions.assertEquals(user.userDetails!!.academicDetails.twelfthInstitute, onboardingContext.twelfthInstitute)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degreeInstitute, onboardingContext.degreeInstitute)
        Assertions.assertEquals(
            user.userDetails!!.academicDetails.twelfthPassOutYear,
            onboardingContext.twelfthPassoutYear
        )
        Assertions.assertEquals(user.userDetails!!.academicDetails.twelfthCourse, onboardingContext.twelfthCourse)
        Assertions.assertEquals(
            user.userDetails!!.academicDetails.twelfthPercentage,
            onboardingContext.twelfthPercentage!!.setScale(2)
        )
        Assertions.assertEquals(
            user.userDetails!!.academicDetails.degreePassOutYear,
            onboardingContext.degreePassoutYear
        )
        Assertions.assertEquals(user.userDetails!!.academicDetails.degree, onboardingContext.degree)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degreeCourse, onboardingContext.degreeCourse)
        Assertions.assertEquals(user.userDetails!!.academicDetails.degreePercentage, onboardingContext.degreePercentage!!.setScale(2, RoundingMode.HALF_UP))
        Assertions.assertEquals(user.userDetails!!.academicDetails.document, "")
        Assertions.assertEquals(user.userDetails!!.gender, onboardingContext.gender)
        Assertions.assertEquals(user.userDetails!!.panNumber, onboardingContext.panNumber)
        Assertions.assertEquals(user.userDetails!!.panDoc, "")
        Assertions.assertEquals(user.userDetails!!.photo, "")
        Assertions.assertEquals(user.userDetails!!.personalEmail, onboardingContext.personalEmail)
        Assertions.assertEquals(user.userDetails!!.bloodGroup, onboardingContext.bloodGroup)
        Assertions.assertEquals(user.userDetails!!.localAddress.area, onboardingContext.localAddress!!.area)
        Assertions.assertEquals(user.userDetails!!.localAddress.city, onboardingContext.localAddress!!.city)
        Assertions.assertEquals(user.userDetails!!.localAddress.district, onboardingContext.localAddress!!.district)
        Assertions.assertEquals(user.userDetails!!.localAddress.state, onboardingContext.localAddress!!.state)
        Assertions.assertEquals(
            user.userDetails!!.localAddress.houseNumber,
            onboardingContext.localAddress!!.houseNumber
        )
        Assertions.assertEquals(user.userDetails!!.localAddress.pincode, onboardingContext.localAddress!!.pincode)
        Assertions.assertEquals(user.userDetails!!.permanentAddress.area, onboardingContext.permanentAddress!!.area)
        Assertions.assertEquals(user.userDetails!!.permanentAddress.city, onboardingContext.permanentAddress!!.city)
        Assertions.assertEquals(
            user.userDetails!!.permanentAddress.district,
            onboardingContext.permanentAddress!!.district
        )
        Assertions.assertEquals(user.userDetails!!.permanentAddress.state, onboardingContext.permanentAddress!!.state)
        Assertions.assertEquals(
            user.userDetails!!.permanentAddress.houseNumber,
            onboardingContext.permanentAddress!!.houseNumber
        )
        Assertions.assertEquals(
            user.userDetails!!.permanentAddress.pincode,
            onboardingContext.permanentAddress!!.pincode
        )
        Assertions.assertEquals(true, user.userDetails!!.leaveDetails != null)
    }

    @Test
    fun shouldReturnExpectedEmployeeCode() {
        val onboardedContext1 =
            onboardingContextRepository.save(
                OnboardedContextGenerator.getOnBoardedContext(
                    personalEmail = "test1@gmail.com", onboardingContextStatus = OnboardingContextStatus.APPROVED
                )
            )
        val createUserRequest = CreateUserRequest(
            onboardingContextId = onboardingContext.id,
            role = Role.EMPLOYEE,
            designation = "designation",
            dateOfJoining = Date(Instant.now().toEpochMilli()),
            reporterId = 1,
            professionalEmail = "professional1@gmail.com"
        )
        val response1 = userService.registerUser(createUserRequest)
        Assertions.assertEquals("VIN2", response1.employeeCode)

        val onboardedContext2 =
            onboardingContextRepository.save(
                OnboardedContextGenerator.getOnBoardedContext(
                    personalEmail = "test2@gmail.com", onboardingContextStatus = OnboardingContextStatus.APPROVED
                )
            )
        val createUserRequest2 = CreateUserRequest(
            onboardingContextId = onboardedContext1.id,
            role = Role.EMPLOYEE,
            designation = "designation",
            dateOfJoining = Date(Instant.now().toEpochMilli()),
            reporterId = 1,
            professionalEmail = "professional2@gmail.com"
        )
        val response2 = userService.registerUser(createUserRequest2)
        Assertions.assertEquals("VIN3", response2.employeeCode)

        val onboardedContext3 =
            onboardingContextRepository.save(
                OnboardedContextGenerator.getOnBoardedContext(
                    personalEmail = "test3@gmail.com", onboardingContextStatus = OnboardingContextStatus.APPROVED
                )
            )

        val createUserRequest3 = CreateUserRequest(
            onboardingContextId = onboardedContext2.id,
            role = Role.EMPLOYEE,
            designation = "designation",
            dateOfJoining = Date(Instant.now().toEpochMilli()),
            reporterId = 1,
            professionalEmail = "professional3@gmail.com"
        )
        val response3 = userService.registerUser(createUserRequest3)
        Assertions.assertEquals("VIN4", response3.employeeCode)

            onboardingContextRepository.save(
                OnboardedContextGenerator.getOnBoardedContext(
                    personalEmail = "test4@gmail.com", onboardingContextStatus = OnboardingContextStatus.APPROVED
                )
            )
        val createUserRequest4 = CreateUserRequest(
            onboardingContextId = onboardedContext3.id,
            role = Role.EMPLOYEE,
            designation = "designation",
            dateOfJoining = Date(Instant.now().toEpochMilli()),
            reporterId = 1,
            professionalEmail = "professional4@gmail.com"
        )
        val response4 = userService.registerUser(createUserRequest4)
        Assertions.assertEquals("VIN5", response4.employeeCode)
    }

    @Test
    fun shouldAddBankDetailsToRegisteredEmployee() {
        val createUserRequest = CreateUserRequest(
            onboardingContextId = onboardingContext.id,
            role = Role.EMPLOYEE,
            designation = "designation",
            dateOfJoining = Date(Instant.now().toEpochMilli()),
            reporterId = 1,
            professionalEmail = "professional@gmail.com"
        )

        val createBankDetailsRequest = CreateBankDetailsRequest(
            accountNumber = 12345678910L,
            ifsc = "SCCCVV",
            bankName = "TestingBank",
            accountHolderName = "account holder name"
        )

        val response = userService.registerUser(createUserRequest)
        var user = userRepository.findByEmployeeCode(response.employeeCode)
        userService.addBankDetails(user.id, createBankDetailsRequest)
        user = userRepository.findByEmployeeCode(response.employeeCode)

        Assertions.assertEquals(12345678910L, user.userDetails!!.bankDetails!!.accountNumber)
        Assertions.assertEquals("SCCCVV", user.userDetails!!.bankDetails!!.ifsc)
        Assertions.assertEquals("TestingBank", user.userDetails!!.bankDetails!!.bankName)
    }

    @Test
    fun shouldThrowExceptionWhenUserNotRegistered() {
        val createBankDetailsRequest = CreateBankDetailsRequest(
            accountNumber = 12345678910L,
            ifsc = "SCCCVV",
            bankName = "TestingBank",
            accountHolderName = "account holder name"
        )
        val exception = Assertions.assertThrows(NotFoundException::class.java) {
            userService.addBankDetails(10001L, createBankDetailsRequest)
        }
        Assertions.assertEquals(USER_NOT_FOUND, exception.error.message)
    }

    @Test
    fun shouldGetAllUser() {
        val createUsersReq: MutableList<CreateUserRequest> = mutableListOf()
        for (i in 1..10) {
            val context = onboardingContextRepository.save(
                OnboardedContextGenerator.getOnBoardedContext(
                    personalEmail = "user${i}@gmail.com", onboardingContextStatus = OnboardingContextStatus.APPROVED
                )
            )
            createUsersReq.add(
                CreateUserRequest(
                    onboardingContextId = context.id,
                    role = Role.EMPLOYEE,
                    designation = "designation",
                    dateOfJoining = Date(Instant.now().toEpochMilli()),
                    reporterId = 1,
                    professionalEmail = "professional${i}@gmail.com"
                )
            )
        }
        for (createUserRequest in createUsersReq) {
            userService.registerUser(createUserRequest)
        }
        val response = userService.getAllUsersWithFilter(0, 5, UserRequestFilter(status = UserStatus.ONBOARDED))
        Assertions.assertEquals(true, response.totalCount >= 10)
        Assertions.assertEquals(5, response.contents.size)
    }

    @Test
    fun shouldThrowNotFoundExceptionForResourceNotFound() {
        val employeeRole = roleRepository.findById(1).get()
        val savedUser = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        val exception: Throwable = Assertions.assertThrows(NotFoundException::class.java) {
            //Throws NotFoundException as user resources has not been added
            userService.fetchUserResourceByUserId(savedUser.id)
        }.error

        Assertions.assertEquals(RESOURCE_NOT_FOUND, exception.message)
    }
}