package com.bluepilot.coreservice.generators

import com.bluepilot.entities.OnboardingContext
import com.bluepilot.entities.UserAddress
import com.bluepilot.enums.OnboardingContextStatus
import java.math.BigDecimal
import java.sql.Date

object OnboardedContextGenerator {
    fun getOnBoardedContext(
        firstName: String = "TestFirst",
        middleName: String = "MiddleFirst",
        lastName: String = "LastTest",
        contactNumber: Long = 1234567890L,
        alternateContactNumber: Long = 1234567890L,
        alternateContactRelation: String = "relation with contact",
        localAddress: UserAddress = getUserAddress(),
        permanentAddress: UserAddress = getUserAddress(),
        dateOfBirth: Date = Date(2022, 3, 23),
        tenthPassoutYear: Int = 1990,
        tenthPercentage: BigDecimal = BigDecimal(50.0),
        tenthInstitue: String = "tenth-institute",
        twelfthPassoutYear: Int = 1992,
        twelfthCourse: String = "Science",
        twelfthPercentage: BigDecimal = BigDecimal(52.0),
        twelfthInstitue: String = "twelfth-institute",
        degreePassoutYear: Int = 1996,
        degree: String = "B.Tech",
        degreeCourse: String = "CSE",
        degreePercentage: BigDecimal = BigDecimal(9.6),
        degreeInstitue: String = "twelfth-institute",
        academicDetailsDocument: String = "urlOfAcademicDetailsDocument",
        gender: String = "M",
        aadhaarNumber: String = "123456789333",
        aadhaarDocument: String = "urlOfAadhaarDocument",
        panNumber: String = "ABCSED12",
        panDocument: String = "urlPanDocument",
        photo: String = "uslOfPhoto",
        personalEmail: String = "test@gmail.com",
        bloodGroup: String = "O+",
        inviteCode: String = generateInviteCode(),
        onboardingContextStatus: OnboardingContextStatus = OnboardingContextStatus.INVITED

    ): OnboardingContext {
        return OnboardingContext(
            firstName = firstName,
            middleName = middleName,
            lastName = lastName,
            contactNumber = contactNumber,
            alternateContactNumber = alternateContactNumber,
            alternateContactRelation = alternateContactRelation,
            localAddress = localAddress,
            permanentAddress = permanentAddress,
            dateOfBirth = dateOfBirth,
            tenthPassoutYear = tenthPassoutYear,
            tenthPercentage = tenthPercentage,
            tenthInstitute = tenthInstitue,
            twelfthPassoutYear = twelfthPassoutYear,
            twelfthCourse = twelfthCourse,
            twelfthPercentage = twelfthPercentage,
            twelfthInstitute = twelfthInstitue,
            degreePassoutYear = degreePassoutYear,
            degree = degree,
            degreeCourse = degreeCourse,
            degreePercentage = degreePercentage,
            degreeInstitute = degreeInstitue,
            academicDetailsDocument = academicDetailsDocument,
            gender = gender,
            aadhaarNumber = aadhaarNumber,
            aadhaarDocument = aadhaarDocument,
            panNumber = panNumber,
            panDocument = panDocument,
            photo = photo,
            personalEmail = personalEmail,
            bloodGroup = bloodGroup,
            inviteCode = inviteCode,
            onboardingContextStatus = onboardingContextStatus
        )
    }
}

fun getUserAddress(
    houseNumber: String = "H1",
    street: String = "9th",
    area: String = "Area",
    city: String = "TestCity",
    district: String = "TestDistrict",
    state: String = "TestState",
    pincode: Long = 12345L,

    ): UserAddress {
    return UserAddress(
        houseNumber = houseNumber,
        street = street,
        area = area,
        city = city,
        district = district,
        state = state,
        pincode = pincode
    )
}

fun generateInviteCode(length: Int = 6): String {
    val allowedChars = ('A'..'Z') + ('0'..'9')
    return "VIN${
        (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }"
}