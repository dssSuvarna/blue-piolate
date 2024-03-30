package com.bluepilot.userservice.models.requests

import jakarta.validation.constraints.Pattern

data class UpdateUserDetailsRequest(
    var firstName: String,
    var middleName: String? = null,
    var lastName: String,
    var contactNumber: Long,
    var alternateContactNumber: Long,
    var alternateContactRelation: String,
    var localAddressHouseNumber: String,
    var localAddressStreet: String,
    var localAddressArea: String,
    var localAddressCity: String,
    var localAddressDistrict: String,
    var localAddressState: String,
    var localAddressPincode: Long,
    var permanentAddressHouseNumber: String,
    var permanentAddressStreet: String,
    var permanentAddressArea: String,
    var permanentAddressCity: String,
    var permanentAddressDistrict: String,
    var permanentAddressState: String,
    var permanentAddressPincode: Long,
    @field:Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\$")
    var personalEmail: String,
    var bloodGroup: String,
)