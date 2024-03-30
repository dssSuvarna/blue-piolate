package com.bluepilot.userservice.services

import com.bluepilot.configs.JwtService
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.Role
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import com.bluepilot.userservice.generators.UpdateESIAndPFDetailsRequestGenerator
import com.bluepilot.userservice.generators.UpdateUserDetailsRequestGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
class UserServiceTest @Autowired constructor(
    val userRepository: UserRepository,
    val userGenerator: UserGenerator,
    val userService: UserService,
    val roleRepository: AuthRoleRepository
) : BaseTestConfig() {

    @MockBean
    lateinit var eventService: EventService

    @Test
    fun shouldGetUserDetailedResponse() {
        val savedUser = userRepository.save(userGenerator.getUser(userName = "username1"))
        val foundUser = userService.getUserDetailedResponseByUserId(savedUser.id)
        assertEquals(savedUser.firstName, foundUser.firstName)
        assertEquals(savedUser.lastName, foundUser.lastName)
        assertEquals(savedUser.employeeCode, foundUser.employeeCode)
        assertEquals(savedUser.userDetails!!.personalEmail, foundUser.personalEmail)
        assertEquals(savedUser.userDetails!!.contactNumber, foundUser.contactNumber)
        assertEquals(savedUser.userDetails!!.localAddress.city, foundUser.localAddress.city)
        assertEquals(savedUser.userDetails!!.localAddress.pincode, foundUser.localAddress.pincode)
        assertEquals(savedUser.userDetails!!.permanentAddress.state, foundUser.permanentAddress.state)
        assertEquals(savedUser.userDetails!!.permanentAddress.pincode, foundUser.permanentAddress.pincode)
    }

    @Test
    fun shouldUpdateUserDetailsByUser() {
        val personalEmail = "Test@Test.com"
        val updatedEmail = "Updated"
        val savedUser =
            userRepository.save(userGenerator.getUser(personalEmail = personalEmail, userName = "username2"))
        val updateUserDetailsRequest =
            UpdateUserDetailsRequestGenerator.getUpdateBasicDetailsRequest(
                personalEmail = updatedEmail
            )
        val token = JwtService.generateToken(savedUser.authUser)
        val userResponse = userService.updateUserDetails(updateUserDetailsRequest, token)
        assertEquals(updatedEmail, userResponse.personalEmail)
    }

    @Test
    fun shouldAddESIAndPFDetailsByUser() {
        Mockito.`when`(eventService.sendEvent(null, NotificationEventType.ESI_AND_PF_UPDATE, emptyMap())).then {  }
        val savedUser = userRepository.save(userGenerator.getUser(userName = "username3"))
        val updateESIAndPFDetailsByUserRequest =
            UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByUserRequest(userId = savedUser.id)
        val esiAndPFResponse = userService.saveESIAndPFDetailsByUser(updateESIAndPFDetailsByUserRequest)

        val foundUserEsiAndPFDetails = userRepository.findById(savedUser.id).get().userDetails!!.esiAndPFDetails
        assertEquals(foundUserEsiAndPFDetails?.adhaarName, esiAndPFResponse.adhaarName)
        assertEquals(foundUserEsiAndPFDetails?.nominee, esiAndPFResponse.nominee)
        assertEquals(foundUserEsiAndPFDetails?.empDob, esiAndPFResponse.empDob)
        assertEquals(foundUserEsiAndPFDetails?.maritalStatus, esiAndPFResponse.maritalStatus)
        assertEquals(foundUserEsiAndPFDetails?.gender, esiAndPFResponse.gender)
        assertEquals(foundUserEsiAndPFDetails?.fatherOrHusbandName, esiAndPFResponse.fatherOrHusbandName)
    }

    @Test
    fun shouldUpdateESIAndPFDetailsByHR() {
        val savedUser = userRepository.save(userGenerator.getUser(userName = "username4"))
        val updateESIAndPFDetailsByHRRequest =
            UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByHRRequest(userId = savedUser.id)
        val esiAndPFResponse = userService.updateESIAndPFDetailsByHR(updateESIAndPFDetailsByHRRequest)

        val foundUserEsiAndPFDetails = userRepository.findById(savedUser.id).get().userDetails!!.esiAndPFDetails!!
        assertEquals(foundUserEsiAndPFDetails.uanNo, esiAndPFResponse.uanNo)
        assertEquals(foundUserEsiAndPFDetails.esicNo, esiAndPFResponse.esicNo)
        assertEquals(foundUserEsiAndPFDetails.bankAccountNo, esiAndPFResponse.bankAccountNo)
        assertEquals(foundUserEsiAndPFDetails.salaryCategory, esiAndPFResponse.salaryCategory)
        assertEquals(foundUserEsiAndPFDetails.basic, esiAndPFResponse.basic!!.setScale(2))
        assertEquals(foundUserEsiAndPFDetails.hra, esiAndPFResponse.hra!!.setScale(2))
        assertEquals(foundUserEsiAndPFDetails.total, esiAndPFResponse.total!!.setScale(2))
    }

    @Test
    fun shouldThrowUserNotFoundException() {
        val exception: Throwable = assertThrows(NotFoundException::class.java) {
            val updateESIAndPFDetailsByHRRequest =
                UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByHRRequest(userId = 0L)
            userService.updateESIAndPFDetailsByHR(updateESIAndPFDetailsByHRRequest)
        }.error
        assertEquals("User not found", exception.message)
    }

    @Test
    fun shouldGetESIAndPFDetailsForUserByToken() {
        Mockito.`when`(eventService.sendEvent(null, NotificationEventType.ESI_AND_PF_UPDATE, emptyMap())).then {  }
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val savedUser = userRepository.save(userGenerator.getUser(userName = "username5", authRole = employeeRole))

        //Adding ESI and PF Details
        val updateESIAndPFDetailsByUserRequest =
            UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByUserRequest(userId = savedUser.id)
        userService.saveESIAndPFDetailsByUser(updateESIAndPFDetailsByUserRequest)
        val updateESIAndPFDetailsByHRRequest =
            UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByHRRequest(userId = savedUser.id)
        userService.updateESIAndPFDetailsByHR(updateESIAndPFDetailsByHRRequest)

        val savedESIAndPfDetails = userRepository.findById(savedUser.id).get().userDetails!!.esiAndPFDetails!!
        val token = JwtService.generateToken(savedUser.authUser)
        val response = userService.getESIAndPFDetailsForUser("Bearer $token")

        assertEquals(response.adhaarName, savedESIAndPfDetails.adhaarName)
        assertEquals(response.uanNo, savedESIAndPfDetails.uanNo)
        assertEquals(response.esicNo, savedESIAndPfDetails.esicNo)
        assertEquals(response.bankName, savedESIAndPfDetails.bankName)
        assertEquals(response.ifscCode, savedESIAndPfDetails.ifscCode)
        assertEquals(response.nominee, savedESIAndPfDetails.nominee)
        assertEquals(response.state, savedESIAndPfDetails.state)
        assertEquals(response.mobNo, savedESIAndPfDetails.mobNo)
        assertEquals(response.panNo, savedESIAndPfDetails.panNo)
        assertEquals(response.gender, savedESIAndPfDetails.gender)
    }

    @Test
    fun shouldFetchESIAndPFDetailsByUserId() {
        Mockito.`when`(eventService.sendEvent(null, NotificationEventType.ESI_AND_PF_UPDATE, emptyMap())).then {  }
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val savedUser = userRepository.save(userGenerator.getUser(userName = "username5", authRole = employeeRole))

        //Adding ESI and PF Details
        val updateESIAndPFDetailsByUserRequest =
            UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByUserRequest(userId = savedUser.id)
        userService.saveESIAndPFDetailsByUser(updateESIAndPFDetailsByUserRequest)
        val updateESIAndPFDetailsByHRRequest =
            UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByHRRequest(userId = savedUser.id)
        userService.updateESIAndPFDetailsByHR(updateESIAndPFDetailsByHRRequest)

        val savedESIAndPfDetails = userRepository.findById(savedUser.id).get().userDetails!!.esiAndPFDetails!!
        val response = userService.getESIAndPFDetailsByUserId(savedUser.id)

        assertEquals(response.fatherName, savedESIAndPfDetails.fatherName)
        assertEquals(response.wifeName, savedESIAndPfDetails.wifeName)
        assertEquals(response.bankAccountNo, savedESIAndPfDetails.bankAccountNo)
        assertEquals(response.pfNoOrPfMemberId, savedESIAndPfDetails.pfNoOrPfMemberId)
        assertEquals(response.empCode, savedESIAndPfDetails.empCode)
        assertEquals(response.maritalStatus, savedESIAndPfDetails.maritalStatus)
        assertEquals(response.landMark, savedESIAndPfDetails.landMark)
        assertEquals(response.hra, savedESIAndPfDetails.hra)
        assertEquals(response.salaryCategory, savedESIAndPfDetails.salaryCategory)
        assertEquals(response.remark, savedESIAndPfDetails.remark)
    }
}