package com.bluepilot.userservice.generators

import com.bluepilot.userservice.models.requests.UpdateUserDetailsRequest

object UpdateUserDetailsRequestGenerator {
        fun getUpdateBasicDetailsRequest(
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
            personalEmail: String = "test@gmail.com",
            bloodGroup: String = "O+"
        ): UpdateUserDetailsRequest {
            return UpdateUserDetailsRequest(
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
                personalEmail = personalEmail,
                bloodGroup = bloodGroup
            )
        }
    }