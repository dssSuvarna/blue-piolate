package com.bluepilot.coreservice.models.requests

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.math.BigDecimal
import java.sql.Date

data class BasicDetailsRequest(
    var id: Long,
    @field:NotNull
    val firstName: String,
    var middleName: String? = null,
    val lastName: String,
    @field:NotNull
    val contactNumber: Long,
    val alternateContactNumber: Long,
    val alternateContactRelation: String,
    val localAddressHouseNumber: String,
    val localAddressStreet: String,
    val localAddressArea: String,
    val localAddressCity: String,
    val localAddressDistrict: String,
    val localAddressState: String,
    val localAddressPincode: Long,
    val permanentAddressHouseNumber: String,
    val permanentAddressStreet: String,
    val permanentAddressArea: String,
    val permanentAddressCity: String,
    val permanentAddressDistrict: String,
    val permanentAddressState: String,
    val permanentAddressPincode: Long,
    @field:NotNull
    val dateOfBirth: Date,
    @field:NotNull
    val tenthPassoutYear: Int,
    @field:NotNull
    val tenthPercentage: BigDecimal,
    @field:NotNull
    val tenthInstitute: String,
    @field:NotNull
    val twelfthPassoutYear: Int,
    @field:NotNull
    val twelfthCourse: String,
    @field:NotNull
    val twelfthPercentage: BigDecimal,
    @field:NotNull
    val twelfthInstitute: String,
    @field:NotNull
    val degreePassoutYear: Int,
    @field:NotNull
    val degree: String,
    @field:NotNull
    val degreeCourse: String,
    @field:NotNull
    val degreePercentage: BigDecimal,
    @field:NotNull
    val degreeInstitute: String,
    @field:NotNull
    val academicDetailsDocument: String,
    @field:NotNull
    val gender: String,
    @field:NotNull
    val aadhaarNumber: String,
    @field:NotNull
    val aadhaarDocument: String,
    @field:NotNull
    val panNumber: String,
    @field:NotNull
    val panDocument: String,
    @field:NotNull
    val photo: String,
    @field:Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\$")
    val personalEmail: String,
    @field:NotNull
    val bloodGroup: String,
)