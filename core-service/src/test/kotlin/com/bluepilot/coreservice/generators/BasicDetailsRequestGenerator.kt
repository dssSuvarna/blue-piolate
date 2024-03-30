package com.bluepilot.coreservice.generators

import com.bluepilot.coreservice.models.requests.BasicDetailsRequest
import java.math.BigDecimal
import java.sql.Date

object BasicDetailsRequestGenerator {
    fun getBasicDetailsRequest(
        id: Long = 0,
        firstName: String = "TestFirst",
        middleName: String = "MiddleFirst",
        lastName: String = "LastTest",
        contactNumber: Long = 1234567890L,
        alternateContactNumber: Long = 1234567890L,
        alternateContactRelation: String = "contact relation",
        localAddressHouseNumber: String = "TestHouseNumber",
        localAddressStreet: String = "TestStreet",
        localAddressArea: String = "TestArea",
        localAddressCity: String = "TestCity",
        localAddressDistrict: String = "TestDistrict",
        localAddressState: String = "TestState",
        localAddressPincode: Long = 123456L,
        permanentAddressHouseNumber: String = "TestHouseNumber",
        permanentAddressStreet: String = "TestStreet",
        permanentAddressArea: String = "TestArea",
        permanentAddressCity: String = "TestCity",
        permanentAddressDistrict: String = "TestDistrict",
        permanentAddressState: String = "TestState",
        permanentAddressPincode: Long = 123456L,
        dateOfBirth: Date = Date(2022, 3, 23),
        tenthPassoutYear: Int = 1990,
        tenthPercentage: BigDecimal = BigDecimal(50.0),
        tenthInstitute: String = "tenth-institute",
        twelfthPassoutYear: Int = 1992,
        twelfthCourse: String = "Science",
        twelfthPercentage: BigDecimal = BigDecimal(52.0),
        twelfthInstitute: String = "tenth-institute",
        degreePassoutYear: Int = 1996,
        degree: String = "B.Tech",
        degreeCourse: String = "CSE",
        degreePercentage: BigDecimal = BigDecimal(9.6),
        degreeInstitute: String = "tenth-institute",
        academicDetailsDocument: String = "urlOfAcademicDetailsDocument",
        gender: String = "M",
        aadhaarNumber: String = "123456789333",
        aadhaarDocument: String = "urlOfAadhaarDocument",
        panNumber: String = "ABCSED12",
        panDocument: String = "urlOfPanDocument",
        photo: String = "urlOfPhoto",
        personalEmail: String = "test@gmail.com",
        bloodGroup: String = "O+"
    ): BasicDetailsRequest {
        return BasicDetailsRequest(
            id = id,
            firstName = firstName,
            middleName = middleName,
            lastName = lastName,
            contactNumber = contactNumber,
            alternateContactNumber = alternateContactNumber,
            alternateContactRelation = alternateContactRelation,
            localAddressHouseNumber = localAddressHouseNumber,
            localAddressStreet = localAddressStreet,
            localAddressArea = localAddressArea,
            localAddressCity = localAddressCity,
            localAddressDistrict = localAddressDistrict,
            localAddressState = localAddressState,
            localAddressPincode = localAddressPincode,
            permanentAddressHouseNumber = permanentAddressHouseNumber,
            permanentAddressStreet = permanentAddressStreet,
            permanentAddressArea = permanentAddressArea,
            permanentAddressCity = permanentAddressCity,
            permanentAddressDistrict = permanentAddressDistrict,
            permanentAddressState = permanentAddressState,
            permanentAddressPincode = permanentAddressPincode,
            dateOfBirth = dateOfBirth,
            tenthPassoutYear = tenthPassoutYear,
            tenthPercentage = tenthPercentage,
            tenthInstitute = tenthInstitute,
            twelfthCourse = twelfthCourse,
            twelfthPassoutYear = twelfthPassoutYear,
            twelfthPercentage = twelfthPercentage,
            twelfthInstitute = twelfthInstitute,
            degree = degree,
            degreePassoutYear = degreePassoutYear,
            degreeCourse = degreeCourse,
            degreePercentage = degreePercentage,
            degreeInstitute = degreeInstitute,
            academicDetailsDocument = academicDetailsDocument,
            gender = gender,
            aadhaarNumber = aadhaarNumber,
            aadhaarDocument = aadhaarDocument,
            panNumber = panNumber,
            panDocument = panDocument,
            photo = photo,
            personalEmail = personalEmail,
            bloodGroup = bloodGroup
        )
    }
}