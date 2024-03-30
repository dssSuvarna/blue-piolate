package com.bluepilot.userservice.models.responses

import com.bluepilot.entities.UserAddress
import com.bluepilot.enums.UserStatus
import java.sql.Date

data class UserDetailedResponse (
    val id: Long,
    val firstName: String,
    val lastName: String,
    val employeeCode: String,
    val designation: String,
    val professionalEmail: String?,
    val personalEmail: String,
    val gender: String,
    val contactNumber: Long,
    val alternateContactNumber: Long,
    val alternateContactRelation: String,
    val dateOfBirth: Date,
    val dateOfJoining: Date,
    val bloodGroup: String,
    val localAddress: UserAddress,
    val permanentAddress: UserAddress,
    val aadhaarNumber: String,
    val panNumber: String,
    val reporter: String,
    val status: UserStatus,
    val profilePicture: String?,
    val salaryDetailsId: Long?,
    val saturdayOff: Boolean
)