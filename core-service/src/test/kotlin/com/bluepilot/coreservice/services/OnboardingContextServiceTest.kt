package com.bluepilot.coreservice.services

import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.coreservice.generators.BasicDetailsRequestGenerator
import com.bluepilot.coreservice.generators.OnboardedContextGenerator
import com.bluepilot.entities.OnboardingContext
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.OnboardingContextStatus
import com.bluepilot.errors.ErrorMessages.Companion.INVALID_EMAIL
import com.bluepilot.errors.ErrorMessages.Companion.INVALID_INVITE_CODE
import com.bluepilot.exceptions.InvalidInviteException
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.OnboardingContextRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.math.RoundingMode

@SpringBootTest
class OnboardingContextServiceTest @Autowired constructor(
    private val onboardingContextService: OnboardingContextService,
    private val onboardingContextRepository: OnboardingContextRepository
) : BaseTestConfig() {

    @MockBean
    lateinit var eventService: EventService

    @Test
    fun shouldReInviteUser() {
        val testEmail = "test1@test1"
        onboardingContextRepository.save(
            OnboardingContext(
                personalEmail = testEmail,
                inviteCode = "testcode1",
                onboardingContextStatus = OnboardingContextStatus.TO_BE_VERIFIED
            )
        )
        Mockito.`when`(eventService.sendEvent(null,NotificationEventType.RE_INVITE_USER, emptyMap())).then {  }
        onboardingContextService.inviteUser(testEmail, "firstname", "comment")
        val foundUser = onboardingContextRepository.findByPersonalEmail(testEmail)!!
        Assertions.assertThat(foundUser.onboardingContextStatus).isEqualTo(OnboardingContextStatus.RE_INVITED)
    }

    @Test
    fun shouldGenerateInviteCode() {
        val inviteCode = onboardingContextService.generateInviteCode()
        Assertions.assertThat(inviteCode).isNotNull().startsWith("VINNO")
    }

    @Test
    fun shouldThrowUserNotFoundException() {
        val email = "test2@test2"
        val invalidEmail = "invalid email"
        val inviteCode = "testcode2"
        onboardingContextRepository.save(
            OnboardingContext(
                personalEmail = email,
                inviteCode = inviteCode,
                onboardingContextStatus = OnboardingContextStatus.INVITED
            )
        )
        val exception = assertThrows(NotFoundException::class.java) {
            onboardingContextService.validateInviteCodeAndGetContext(inviteCode, invalidEmail)
        }
        assertEquals(INVALID_EMAIL, exception.error.message)
    }

    @Test
    fun shouldThrowInvalidInviteException() {
        val email = "test3@test3"
        val inviteCode = "testcode2"
        val invalidInviteCode = "invalid code"
        onboardingContextRepository.save(
            OnboardingContext(
                personalEmail = email,
                inviteCode = inviteCode,
                onboardingContextStatus = OnboardingContextStatus.INVITED
            )
        )
        val exception = assertThrows(InvalidInviteException::class.java) {
            onboardingContextService.validateInviteCodeAndGetContext(invalidInviteCode, email)
        }
        assertEquals(INVALID_INVITE_CODE, exception.error.message)
    }

    @Test
    fun shouldSaveBasicDetails() {
        val basicDetailsRequest = BasicDetailsRequestGenerator.getBasicDetailsRequest()
        val context =
            onboardingContextService.saveInviteDetails(basicDetailsRequest.personalEmail, basicDetailsRequest.firstName)
        basicDetailsRequest.id = context.id
        onboardingContextService.saveBasicDetails(basicDetailsRequest)
        val onboardingContext = onboardingContextRepository.findByPersonalEmail(basicDetailsRequest.personalEmail)!!
        assertEquals(basicDetailsRequest.firstName, onboardingContext.firstName)
        assertEquals(basicDetailsRequest.lastName, onboardingContext.lastName)
        assertEquals(basicDetailsRequest.aadhaarNumber, onboardingContext.aadhaarNumber)
        assertEquals(basicDetailsRequest.aadhaarDocument, onboardingContext.aadhaarDocument)
        assertEquals(basicDetailsRequest.contactNumber, onboardingContext.contactNumber)
        assertEquals(basicDetailsRequest.alternateContactNumber, onboardingContext.alternateContactNumber)
        assertEquals(basicDetailsRequest.dateOfBirth, onboardingContext.dateOfBirth)
        assertEquals(basicDetailsRequest.tenthPassoutYear, onboardingContext.tenthPassoutYear)
        assertEquals(basicDetailsRequest.tenthPercentage.setScale(2), onboardingContext.tenthPercentage)
        assertEquals(basicDetailsRequest.twelfthPassoutYear, onboardingContext.twelfthPassoutYear)
        assertEquals(basicDetailsRequest.twelfthCourse, onboardingContext.twelfthCourse)
        assertEquals(basicDetailsRequest.twelfthPercentage.setScale(2), onboardingContext.twelfthPercentage)
        assertEquals(basicDetailsRequest.degreePassoutYear, onboardingContext.degreePassoutYear)
        assertEquals(basicDetailsRequest.degree, onboardingContext.degree)
        assertEquals(basicDetailsRequest.degreeCourse, onboardingContext.degreeCourse)
        assertEquals(basicDetailsRequest.degreePercentage.setScale(2, RoundingMode.HALF_UP), onboardingContext.degreePercentage)
        assertEquals(basicDetailsRequest.academicDetailsDocument, onboardingContext.academicDetailsDocument)
        assertEquals(basicDetailsRequest.gender, onboardingContext.gender)
        assertEquals(basicDetailsRequest.panNumber, onboardingContext.panNumber)
        assertEquals(basicDetailsRequest.panDocument, onboardingContext.panDocument)
        assertEquals(basicDetailsRequest.photo, onboardingContext.photo)
        assertEquals(basicDetailsRequest.personalEmail, onboardingContext.personalEmail)
        assertEquals(basicDetailsRequest.bloodGroup, onboardingContext.bloodGroup)
        assertEquals(basicDetailsRequest.localAddressArea, onboardingContext.localAddress!!.area)
        assertEquals(basicDetailsRequest.localAddressCity, onboardingContext.localAddress!!.city)
        assertEquals(basicDetailsRequest.localAddressDistrict, onboardingContext.localAddress!!.district)
        assertEquals(basicDetailsRequest.localAddressStreet, onboardingContext.localAddress!!.street)
        assertEquals(basicDetailsRequest.localAddressState, onboardingContext.localAddress!!.state)
        assertEquals(basicDetailsRequest.localAddressHouseNumber, onboardingContext.localAddress!!.houseNumber)
        assertEquals(basicDetailsRequest.localAddressPincode, onboardingContext.localAddress!!.pincode)
        assertEquals(basicDetailsRequest.permanentAddressArea, onboardingContext.permanentAddress!!.area)
        assertEquals(basicDetailsRequest.permanentAddressCity, onboardingContext.permanentAddress!!.city)
        assertEquals(
            basicDetailsRequest.permanentAddressDistrict,
            onboardingContext.permanentAddress!!.district
        )
        assertEquals(basicDetailsRequest.permanentAddressState, onboardingContext.permanentAddress!!.state)
        assertEquals(basicDetailsRequest.permanentAddressStreet, onboardingContext.permanentAddress!!.street)
        assertEquals(
            basicDetailsRequest.permanentAddressHouseNumber,
            onboardingContext.permanentAddress!!.houseNumber
        )
        assertEquals(basicDetailsRequest.permanentAddressPincode, onboardingContext.permanentAddress!!.pincode)
        assertEquals(basicDetailsRequest.tenthInstitute, onboardingContext.tenthInstitute)
        assertEquals(basicDetailsRequest.twelfthInstitute, onboardingContext.twelfthInstitute)
        assertEquals(basicDetailsRequest.degreeInstitute, onboardingContext.degreeInstitute)
    }

    @Test
    fun shouldApproveBasicDetails() {
        val context = onboardingContextService.saveInviteDetails("Test@gmail.com", "test")
        onboardingContextService.updateOnboardingContextStatus(
            context.id,
            OnboardingContextStatus.APPROVED.name
        )
        val onboardingContext = onboardingContextRepository.findById(context.id).get()
        assertEquals(OnboardingContextStatus.APPROVED, onboardingContext.onboardingContextStatus)
    }

    @Test
    fun shouldGetAllOnboardingContexts() {
        for (i in 1..15) {
            onboardingContextRepository.save(
                OnboardedContextGenerator.getOnBoardedContext(personalEmail = "user${i}@gmail.com")
            )
        }
        val response = onboardingContextService.getAllOnboardingContexts(0, 5)
        assertEquals(true, response.totalCount >= 15)
        assertEquals(5, response.contents.size)
    }
}