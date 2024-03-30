package com.bluepilot.coreservice.mappers

import com.bluepilot.coreservice.models.requests.BasicDetailsRequest
import com.bluepilot.coreservice.models.responses.BasicDetailsResponse
import com.bluepilot.entities.OnboardingContext
import com.bluepilot.entities.UserAddress
import com.bluepilot.enums.OnboardingContextStatus
import org.springframework.stereotype.Component

@Component
class OnboardingContextMapper {
    fun map(basicDetailsRequest: BasicDetailsRequest, context: OnboardingContext): OnboardingContext {
        return context.apply {
            firstName = basicDetailsRequest.firstName
            middleName = basicDetailsRequest.middleName
            lastName = basicDetailsRequest.lastName
            contactNumber = basicDetailsRequest.contactNumber
            alternateContactNumber = basicDetailsRequest.alternateContactNumber
            alternateContactRelation = basicDetailsRequest.alternateContactRelation
            localAddress = getLocalAddress(basicDetailsRequest)
            permanentAddress = getPermanentAddress(basicDetailsRequest)
            dateOfBirth = basicDetailsRequest.dateOfBirth
            tenthPassoutYear = basicDetailsRequest.tenthPassoutYear
            tenthPercentage = basicDetailsRequest.tenthPercentage
            tenthInstitute = basicDetailsRequest.tenthInstitute
            twelfthPassoutYear = basicDetailsRequest.twelfthPassoutYear
            twelfthCourse = basicDetailsRequest.twelfthCourse
            twelfthInstitute = basicDetailsRequest.twelfthInstitute
            twelfthPercentage = basicDetailsRequest.twelfthPercentage
            degreePassoutYear = basicDetailsRequest.degreePassoutYear
            degree = basicDetailsRequest.degree
            degreeCourse = basicDetailsRequest.degreeCourse
            degreePercentage = basicDetailsRequest.degreePercentage
            degreeInstitute = basicDetailsRequest.degreeInstitute
            academicDetailsDocument = basicDetailsRequest.academicDetailsDocument
            gender = basicDetailsRequest.gender
            aadhaarNumber = basicDetailsRequest.aadhaarNumber
            aadhaarDocument = basicDetailsRequest.aadhaarDocument
            panNumber = basicDetailsRequest.panNumber
            panDocument = basicDetailsRequest.panDocument
            photo = basicDetailsRequest.photo
            bloodGroup = basicDetailsRequest.bloodGroup
            onboardingContextStatus = OnboardingContextStatus.TO_BE_VERIFIED
        }
    }

    fun map(context: OnboardingContext): BasicDetailsResponse {
        return BasicDetailsResponse(
        id = context.id,
        firstName = context.firstName,
        middleName = context.middleName,
        lastName = context.lastName,
        onboardingContextStatus = context.onboardingContextStatus,
        contactNumber = context.contactNumber,
        alternateContactNumber = context.alternateContactNumber,
        alternateContactRelation = context.alternateContactRelation,
        localAddressHouseNumber = context.localAddress?.houseNumber,
        localAddressStreet = context.localAddress?.street,
        localAddressArea = context.localAddress?.area,
        localAddressCity = context.localAddress?.city,
        localAddressDistrict = context.localAddress?.district,
        localAddressState = context.localAddress?.state,
        localAddressPincode = context.localAddress?.pincode,
        permanentAddressHouseNumber = context.permanentAddress?.houseNumber,
        permanentAddressStreet = context.permanentAddress?.street,
        permanentAddressArea = context.permanentAddress?.area,
        permanentAddressCity = context.permanentAddress?.city,
        permanentAddressDistrict = context.permanentAddress?.district,
        permanentAddressState = context.permanentAddress?.state,
        permanentAddressPincode = context.permanentAddress?.pincode,
        dateOfBirth = context.dateOfBirth,
        tenthPassoutYear = context.tenthPassoutYear,
        tenthPercentage = context.tenthPercentage,
        tenthInstitute = context.tenthInstitute,
        twelfthPassoutYear = context.twelfthPassoutYear,
        twelfthCourse = context.twelfthCourse,
        twelfthPercentage = context.twelfthPercentage,
        twelfthInstitute = context.tenthInstitute,
        degreePassoutYear = context.degreePassoutYear,
        degree = context.degree,
        degreeCourse = context.degreeCourse,
        degreePercentage = context.degreePercentage,
        degreeInstitute = context.degreeInstitute,
        academicDetailsDocument= context.academicDetailsDocument,
        gender = context.gender,
        aadhaarNumber = context.aadhaarNumber,
        aadhaarDocument = context.aadhaarDocument,
        panNumber = context.panNumber,
        panDocument = context.panDocument,
        photo = context.photo,
        personalEmail = context.personalEmail,
        bloodGroup = context.bloodGroup,
        )
    }

    private fun getLocalAddress(basicDetailsRequest: BasicDetailsRequest): UserAddress {
        return UserAddress(
                houseNumber = basicDetailsRequest.localAddressHouseNumber,
                street = basicDetailsRequest.localAddressStreet,
                area = basicDetailsRequest.localAddressArea,
                city = basicDetailsRequest.localAddressCity,
                district = basicDetailsRequest.localAddressDistrict,
                state = basicDetailsRequest.localAddressState,
                pincode = basicDetailsRequest.localAddressPincode
        )
    }

    private fun getPermanentAddress(basicDetailsRequest: BasicDetailsRequest): UserAddress {
        return UserAddress(
                houseNumber = basicDetailsRequest.permanentAddressHouseNumber,
                street = basicDetailsRequest.permanentAddressStreet,
                area = basicDetailsRequest.permanentAddressArea,
                city = basicDetailsRequest.permanentAddressCity,
                district = basicDetailsRequest.permanentAddressDistrict,
                state = basicDetailsRequest.permanentAddressState,
                pincode = basicDetailsRequest.permanentAddressPincode
        )
    }
}