package com.bluepilot.coreservice.models.responses

import com.bluepilot.enums.OnboardingContextStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.sql.Date

data class BasicDetailsResponse(
    @JsonProperty("id")
    var id: Long,
    @JsonProperty("firstName")
    val firstName: String?,
    @JsonProperty("middleName")
    var middleName: String?,
    @JsonProperty("lastName")
    val lastName: String?,
    @JsonProperty("onboardingContextStatus")
    val onboardingContextStatus: OnboardingContextStatus?,
    @JsonProperty("contactNumber")
    val contactNumber: Long?,
    @JsonProperty("alternateContactNumber")
    val alternateContactNumber: Long?,
    @JsonProperty("alternateContactRelation")
    var alternateContactRelation: String?,
    @JsonProperty("localAddressHouseNumber")
    val localAddressHouseNumber: String?,
    @JsonProperty("localAddressStreet")
    val localAddressStreet: String?,
    @JsonProperty("localAddressArea")
    val localAddressArea: String?,
    @JsonProperty("localAddressCity")
    val localAddressCity: String?,
    @JsonProperty("localAddressDistrict")
    val localAddressDistrict: String?,
    @JsonProperty("localAddressState")
    val localAddressState: String?,
    @JsonProperty("localAddressPincode")
    val localAddressPincode: Long?,
    @JsonProperty("permanentAddressHouseNumber")
    val permanentAddressHouseNumber: String?,
    @JsonProperty("permanentAddressStreet")
    val permanentAddressStreet: String?,
    @JsonProperty("permanentAddressArea")
    val permanentAddressArea: String?,
    @JsonProperty("permanentAddressCity")
    val permanentAddressCity: String?,
    @JsonProperty("permanentAddressDistrict")
    val permanentAddressDistrict: String?,
    @JsonProperty("permanentAddressState")
    val permanentAddressState: String?,
    @JsonProperty("permanentAddressPincode")
    val permanentAddressPincode: Long?,
    @JsonProperty("dateOfBirth")
    val dateOfBirth: Date?,
    @JsonProperty("tenthPassoutYear")
    val tenthPassoutYear: Int?,
    @JsonProperty("tenthPercentage")
    val tenthPercentage: BigDecimal?,
    @JsonProperty("tenthInstitute")
    val tenthInstitute: String?,
    @JsonProperty("twelfthPassoutYear")
    val twelfthPassoutYear: Int?,
    @JsonProperty("twelfthCourse")
    val twelfthCourse: String?,
    @JsonProperty("twelfthPercentage")
    val twelfthPercentage: BigDecimal?,
    @JsonProperty("twelfthInstitute")
    val twelfthInstitute: String?,
    @JsonProperty("degreePassoutYear")
    val degreePassoutYear: Int?,
    @JsonProperty("degree")
    val degree: String?,
    @JsonProperty("degreeCourse")
    val degreeCourse: String?,
    @JsonProperty("degreePercentage")
    val degreePercentage: BigDecimal?,
    @JsonProperty("degreeInstitute")
    val degreeInstitute: String?,
    @JsonProperty("academicDetailsDocument")
    val academicDetailsDocument: String?,
    @JsonProperty("gender")
    val gender: String?,
    @JsonProperty("aadhaarNumber")
    val aadhaarNumber: String?,
    @JsonProperty("aadhaarDocument")
    val aadhaarDocument: String?,
    @JsonProperty("panNumber")
    val panNumber: String?,
    @JsonProperty("panDocument")
    val panDocument: String?,
    @JsonProperty("photo")
    val photo: String?,
    @JsonProperty("personalEmail")
    val personalEmail: String?,
    @JsonProperty("bloodGroup")
    val bloodGroup: String?,
)
